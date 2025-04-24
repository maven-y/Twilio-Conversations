package com.twilio.conversations.app.manager

import android.content.Context
import android.util.Log
import com.twilio.video.*
import com.twilio.voice.Call
import com.twilio.voice.CallException
import com.twilio.voice.ConnectOptions
import com.twilio.voice.Voice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.widget.Toast

class CallManager(private val context: Context) {
    private var activeVoiceCall: Call? = null
    private var activeVideoRoom: Room? = null
    private var remoteParticipant: RemoteParticipant? = null
    
    private val _callState = MutableStateFlow<CallState>(CallState.Idle)
    val callState: StateFlow<CallState> = _callState

    // Voice Call Methods
    fun makeVoiceCall(to: String, token: String) {
        val connectOptions = ConnectOptions.Builder(token)
            .params(mapOf("to" to to))
            .build()

        activeVoiceCall = Voice.connect(context, connectOptions, object : Call.Listener {
            override fun onConnectFailure(call: Call, callException: CallException) {
                Toast.makeText(context, "Call failed: ${callException.message}", Toast.LENGTH_SHORT).show()
                activeVoiceCall = null
            }

            override fun onConnected(call: Call) {
                Toast.makeText(context, "Call connected", Toast.LENGTH_SHORT).show()
            }

            override fun onReconnecting(call: Call, callException: CallException) {
                TODO("Not yet implemented")
            }

            override fun onReconnected(call: Call) {
                TODO("Not yet implemented")
            }

            override fun onDisconnected(call: Call, callException: CallException?) {
                callException?.let {
                    Toast.makeText(context, "Call disconnected: ${it.message}", Toast.LENGTH_SHORT).show()
                }
                activeVoiceCall = null
            }

            override fun onRinging(call: Call) {
                Toast.makeText(context, "Ringing...", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun endVoiceCall() {
        activeVoiceCall?.disconnect()
        activeVoiceCall = null
        _callState.value = CallState.Idle
    }

    // Video Call Methods
    fun startVideoCall(roomName: String, token: String) {
        val connectOptions = com.twilio.video.ConnectOptions.Builder(token)
            .roomName(roomName)
            .enableAutomaticSubscription(true)
            .enableDominantSpeaker(true)
            .build()

        activeVideoRoom = Video.connect(context, connectOptions, roomListener)
    }

    private val roomListener = object : Room.Listener {
        override fun onConnected(room: Room) {
            Log.d(TAG, "Connected to room ${room.name}")
            activeVideoRoom = room
            _callState.value = CallState.Connected(CallType.VIDEO)

            // Set the first participant as remote participant
            if (room.remoteParticipants.isNotEmpty()) {
                remoteParticipant = room.remoteParticipants[0]
                remoteParticipant?.setListener(remoteParticipantListener)
            }
        }

        override fun onConnectFailure(room: Room, twilioException: TwilioException) {
            _callState.value = CallState.Failed(twilioException.message ?: "Connection failed")
        }

        override fun onReconnecting(room: Room, twilioException: TwilioException) {
            _callState.value = CallState.Reconnecting
        }

        override fun onReconnected(room: Room) {
            _callState.value = CallState.Connected(CallType.VIDEO)
        }

        override fun onDisconnected(room: Room, exception: TwilioException?) {
            Log.d(TAG, "Disconnected from room ${room.name}")
            activeVideoRoom = null
            remoteParticipant = null
            _callState.value = CallState.Disconnected(exception?.message)
        }

        override fun onParticipantConnected(room: Room, participant: RemoteParticipant) {
            Log.d(TAG, "Participant ${participant.identity} connected")
            remoteParticipant = participant
            remoteParticipant?.setListener(remoteParticipantListener)
        }

        override fun onParticipantDisconnected(room: Room, participant: RemoteParticipant) {
            Log.d(TAG, "Participant ${participant.identity} disconnected")
            if (remoteParticipant == participant) {
                remoteParticipant = null
            }
        }

        override fun onRecordingStarted(room: Room) {
            Log.d(TAG, "Recording started in room ${room.name}")
        }

        override fun onRecordingStopped(room: Room) {
            Log.d(TAG, "Recording stopped in room ${room.name}")
        }
    }

    private val remoteParticipantListener = object : RemoteParticipant.Listener {
        override fun onAudioTrackPublished(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {
            Log.d(TAG, "Audio track published by ${remoteParticipant.identity}")
        }

        override fun onAudioTrackUnpublished(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {
            Log.d(TAG, "Audio track unpublished by ${remoteParticipant.identity}")
        }

        override fun onAudioTrackSubscribed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            remoteAudioTrack: RemoteAudioTrack
        ) {
            Log.d(TAG, "Audio track subscribed")
        }

        override fun onAudioTrackSubscriptionFailed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            twilioException: TwilioException
        ) {
            Log.e(TAG, "Audio track subscription failed: ${twilioException.message}")
        }

        override fun onAudioTrackUnsubscribed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            remoteAudioTrack: RemoteAudioTrack
        ) {
            Log.d(TAG, "Audio track unsubscribed")
        }

        override fun onVideoTrackPublished(
            participant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {
            Log.d(TAG, "Video track published by ${participant.identity}")
        }

        override fun onVideoTrackUnpublished(
            participant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {
            Log.d(TAG, "Video track unpublished by ${participant.identity}")
        }

        override fun onVideoTrackSubscribed(
            participant: RemoteParticipant,
            publication: RemoteVideoTrackPublication,
            remoteVideoTrack: RemoteVideoTrack
        ) {
            Log.d(TAG, "Video track subscribed")
            _callState.value = CallState.Connected(CallType.VIDEO)
        }

        override fun onVideoTrackSubscriptionFailed(
            participant: RemoteParticipant,
            publication: RemoteVideoTrackPublication,
            twilioException: TwilioException
        ) {
            Log.e(TAG, "Video track subscription failed: ${twilioException.message}")
        }

        override fun onVideoTrackUnsubscribed(
            participant: RemoteParticipant,
            publication: RemoteVideoTrackPublication,
            remoteVideoTrack: RemoteVideoTrack
        ) {
            Log.d(TAG, "Video track unsubscribed")
        }

        override fun onDataTrackPublished(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication
        ) {
            Log.d(TAG, "Data track published")
        }

        override fun onDataTrackUnpublished(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication
        ) {
            Log.d(TAG, "Data track unpublished")
        }

        override fun onDataTrackSubscribed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            remoteDataTrack: RemoteDataTrack
        ) {
            Log.d(TAG, "Data track subscribed")
        }

        override fun onDataTrackSubscriptionFailed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            twilioException: TwilioException
        ) {
            Log.e(TAG, "Data track subscription failed: ${twilioException.message}")
        }

        override fun onDataTrackUnsubscribed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            remoteDataTrack: RemoteDataTrack
        ) {
            Log.d(TAG, "Data track unsubscribed")
        }

        override fun onAudioTrackEnabled(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {
            Log.d(TAG, "Audio track enabled")
        }

        override fun onAudioTrackDisabled(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {
            Log.d(TAG, "Audio track disabled")
        }

        override fun onVideoTrackEnabled(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {
            Log.d(TAG, "Video track enabled")
        }

        override fun onVideoTrackDisabled(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {
            Log.d(TAG, "Video track disabled")
        }
    }

    fun endVideoCall() {
        activeVideoRoom?.disconnect()
        activeVideoRoom = null
        remoteParticipant = null
        _callState.value = CallState.Idle
    }

    // Call States
    sealed class CallState {
        object Idle : CallState()
        object Ringing : CallState()
        object Reconnecting : CallState()
        data class Connected(val type: CallType) : CallState()
        data class Disconnected(val reason: String?) : CallState()
        data class Failed(val error: String) : CallState()
    }

    enum class CallType {
        VOICE,
        VIDEO
    }

    companion object {
        private const val TAG = "CallManager"
    }
} 