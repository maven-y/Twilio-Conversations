package com.twilio.conversations.app.ui.call

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.twilio.conversations.app.R
import com.twilio.conversations.app.databinding.ActivityVoiceCallBinding
import com.twilio.conversations.app.manager.CallManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class VoiceCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVoiceCallBinding
    private lateinit var callManager: CallManager

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
        private const val EXTRA_PARTICIPANT = "extra_participant"
        private const val EXTRA_TOKEN = "extra_token"

        fun start(context: Context, participant: String, token: String) {
            val intent = Intent(context, VoiceCallActivity::class.java).apply {
                putExtra(EXTRA_PARTICIPANT, participant)
                putExtra(EXTRA_TOKEN, token)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callManager = CallManager(this)

        setupUI()
        checkPermissions()
        observeCallState()
    }

    private fun setupUI() {
        binding.endCallButton.setOnClickListener {
            callManager.endVoiceCall()
            finish()
        }

        binding.toggleMuteButton.setOnClickListener {
            // Implement mute functionality
        }

        binding.toggleSpeakerButton.setOnClickListener {
            // Implement speaker toggle
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_CONNECT
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, PERMISSION_REQUEST_CODE)
        } else {
            startCall()
        }
    }

    private fun startCall() {
        val participant = intent.getStringExtra(EXTRA_PARTICIPANT)
        val token = intent.getStringExtra(EXTRA_TOKEN)

        if (participant != null && token != null) {
            callManager.makeVoiceCall(participant, token)
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
                    }
                    is CallManager.CallState.Disconnected -> {
                        binding.callStatus.text = "Call ended"
                        finish()
                    }
                    is CallManager.CallState.Failed -> {
                        Toast.makeText(this@VoiceCallActivity, state.error, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else -> {
                        binding.callStatus.text = "Connecting..."
                    }
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
                startCall()
            } else {
                Toast.makeText(this, "Permissions required for voice call", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callManager.endVoiceCall()
    }
} 