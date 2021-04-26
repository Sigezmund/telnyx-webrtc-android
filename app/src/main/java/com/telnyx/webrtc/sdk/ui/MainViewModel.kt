package com.telnyx.webrtc.sdk.ui

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.telnyx.webrtc.sdk.*
import com.telnyx.webrtc.sdk.manager.UserManager
import com.telnyx.webrtc.sdk.model.AudioDevice
import com.telnyx.webrtc.sdk.model.CallState
import com.telnyx.webrtc.sdk.socket.TxSocket
import com.telnyx.webrtc.sdk.verto.receive.ReceivedMessageBody
import com.telnyx.webrtc.sdk.verto.receive.SocketResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.util.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {

    private var socketConnection: TxSocket? = null
    private var telnyxClient: TelnyxClient? = null

    private var currentCall: Call? = null
    private var previousCall: Call? = null

    private var calls: Map<UUID, Call> = mapOf()

    fun initConnection(context: Context) {
        socketConnection = TxSocket(
            host_address = "rtc.telnyx.com",
            port = 14938
        )
        telnyxClient = TelnyxClient(context, socketConnection!!)
        telnyxClient!!.connect()
    }

    fun saveUserData(
        userName: String,
        password: String,
        fcmToken: String?,
        callerIdName: String,
        callerIdNumber: String
    ) {
        if (!userManager.isUserLogin) {
            userManager.isUserLogin = true
            userManager.sipUsername = userName
            userManager.sipPass = password
            userManager.fcmToken = fcmToken
            userManager.calledIdName = callerIdName
            userManager.callerIdNumber = callerIdNumber
        }
    }

    fun getSocketResponse(): LiveData<SocketResponse<ReceivedMessageBody>>? =
        telnyxClient?.getSocketResponse()

    fun setCurrentCall(callId: UUID) {
        calls = telnyxClient?.getActiveCalls()!!
        if (calls.size > 1) {
            previousCall = currentCall
        }
        currentCall = calls[callId]
    }

    fun getCallState(): LiveData<CallState>? = currentCall?.getCallState()
    fun getIsMuteStatus(): LiveData<Boolean>? = currentCall?.getIsMuteStatus()
    fun getIsOnHoldStatus(): LiveData<Boolean>? = currentCall?.getIsOnHoldStatus()
    fun getIsOnLoudSpeakerStatus(): LiveData<Boolean>? = currentCall?.getIsOnLoudSpeakerStatus()

    fun doLoginWithCredentials(credentialConfig: CredentialConfig) {
        telnyxClient?.credentialLogin(credentialConfig)
    }

    fun doLoginWithToken(tokenConfig: TokenConfig) {
        telnyxClient?.tokenLogin(tokenConfig)
    }

    fun sendInvite(
        callerName: String,
        callerNumber: String,
        destinationNumber: String,
        clientState: String
    ) {
        telnyxClient?.call?.newInvite(callerName, callerNumber, destinationNumber, clientState)
    }

    fun acceptCall(callId: UUID, destinationNumber: String) {
        telnyxClient?.call?.acceptCall(callId, destinationNumber)
    }

    fun endCall(callId: UUID) {
        telnyxClient?.call?.endCall(callId)
        previousCall?.let {
            currentCall = it
        }
    }

    fun onHoldUnholdPressed(callId: UUID) {
        currentCall?.onHoldUnholdPressed(callId)
    }

    fun onMuteUnmutePressed() {
        currentCall?.onMuteUnmutePressed()
    }

    fun onLoudSpeakerPressed() {
        currentCall?.onLoudSpeakerPressed()
    }

    fun disconnect() {
        telnyxClient?.disconnect()
        userManager.isUserLogin = false
    }

    fun changeAudioOutput(audioDevice: AudioDevice) {
        telnyxClient?.setAudioOutputDevice(audioDevice)
    }
}
