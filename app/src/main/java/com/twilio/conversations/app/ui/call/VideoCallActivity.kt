package com.twilio.conversations.app.ui.call

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.twilio.conversations.app.databinding.ActivityVideoCallBinding
import com.twilio.conversations.app.manager.CallManager
import com.twilio.video.*
import kotlinx.coroutines.launch
import timber.log.Timber


class VideoCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoCallBinding
    private lateinit var callManager: CallManager
    private var localVideoTrack: LocalVideoTrack? = null
    private var localAudioTrack: LocalAudioTrack? = null
    private var cameraCapturer: Camera2Capturer? = null
    private var remoteParticipant: RemoteParticipant? = null

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
        private const val EXTRA_ROOM_NAME = "extra_room_name"
        private const val EXTRA_TOKEN = "extra_token"

        fun start(context: Context, roomName: String, token: String) {
            val intent = Intent(context, VideoCallActivity::class.java).apply {
                putExtra(EXTRA_ROOM_NAME, roomName)
                putExtra(EXTRA_TOKEN, token)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callManager = CallManager(this)
        setupUI()
        checkPermissions()
        observeCallState()
    }

    private fun getBackCameraId(): String {
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)
                val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    return cameraId
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun setupUI() {
        binding.endCallButton.setOnClickListener {
            callManager.endVideoCall()
            finish()
        }

        binding.toggleCameraButton.setOnClickListener {
            localVideoTrack?.enable(!localVideoTrack!!.isEnabled)
            binding.toggleCameraButton.isSelected = localVideoTrack?.isEnabled == false
        }

        binding.toggleMicButton.setOnClickListener {
            localAudioTrack?.enable(!localAudioTrack!!.isEnabled)
            binding.toggleMicButton.isSelected = localAudioTrack?.isEnabled == false
        }

        binding.switchCameraButton.setOnClickListener {
            cameraCapturer?.switchCamera(getBackCameraId())
        }

        binding.toggleSpeakerButton.setOnClickListener {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
            val isSpeakerOn = audioManager.isSpeakerphoneOn
            audioManager.isSpeakerphoneOn = !isSpeakerOn
            binding.toggleSpeakerButton.isSelected = !isSpeakerOn
        }
    }

    private fun initializeMedia() {
        try {
            // Create camera capturer
            cameraCapturer = Camera2Capturer(this, getBackCameraId())
            
            // Create local audio and video tracks
            localAudioTrack = LocalAudioTrack.create(this, true)
            localVideoTrack = LocalVideoTrack.create(this, true, cameraCapturer!!)
            
            // Set local video view
            localVideoTrack?.addSink(binding.thumbnailVideoView)
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize media")
            Toast.makeText(this, "Failed to initialize media: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, PERMISSION_REQUEST_CODE)
        } else {
            initializeMedia()
            startCall()
        }
    }

    private fun startCall() {
        val roomName = intent.getStringExtra(EXTRA_ROOM_NAME)
        val token = intent.getStringExtra(EXTRA_TOKEN)

        if (roomName != null && token != null) {
            callManager.startVideoCall(roomName, token)
        } else {
            Toast.makeText(this, "Invalid call parameters", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun observeCallState() {
        lifecycleScope.launch {
            callManager.callState.collect { state ->
                when (state) {
                    is CallManager.CallState.Connected -> {
                        binding.callStatus.text = "Connected"
                        handleConnectedState(state)
                    }
                    is CallManager.CallState.Disconnected -> {
                        binding.callStatus.text = "Call ended"
                        finish()
                    }
                    is CallManager.CallState.Failed -> {
                        Toast.makeText(this@VideoCallActivity, state.error, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is CallManager.CallState.Reconnecting -> {
                        binding.callStatus.text = "Reconnecting..."
                    }
                    else -> {
                        binding.callStatus.text = "Connecting..."
                    }
                }
            }
        }
    }

    private fun handleConnectedState(state: CallManager.CallState.Connected) {
        // Handle remote participant's video
        remoteParticipant?.let { participant ->
            participant.remoteVideoTracks.forEach { videoTrackPublication ->
                if (videoTrackPublication.isTrackSubscribed) {
                    videoTrackPublication.remoteVideoTrack?.addSink(binding.primaryVideoView)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initializeMedia()
                startCall()
            } else {
                Toast.makeText(this, "Permissions required for video call", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        
        // Clean up video tracks
        localVideoTrack?.release()
        localAudioTrack?.release()
        
        // Clean up camera
        cameraCapturer = null
        
        callManager.endVideoCall()
    }
} 