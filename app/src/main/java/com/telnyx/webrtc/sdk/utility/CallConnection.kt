package com.telnyx.webrtc.sdk.utility

import android.content.Context
import android.content.Intent
import android.telecom.CallAudioState
import android.telecom.Connection
import android.telecom.DisconnectCause
import com.telnyx.webrtc.sdk.Call
import com.telnyx.webrtc.sdk.TelnyxClient
import com.telnyx.webrtc.sdk.ui.MainActivity
import timber.log.Timber

class CallConnection(context: Context, call: Call, telnyxClient: TelnyxClient) : Connection() {

    private var telnyxCall: Call = call
    private var telnyxClient: TelnyxClient = telnyxClient
    private var connectionContext: Context = context

    override fun onCallAudioStateChanged(state: CallAudioState?) {
        Timber.e("onCallAudioStateChange: %s", state.toString())
    }

    override fun onDisconnect() {
        super.onDisconnect()
        destroyConnection()
        Timber.e("onDisconnect")
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL, "Missed"))
        if (telnyxClient.getActiveCalls().isNotEmpty())
            onDisconnect(telnyxClient.getActiveCalls().values.first())
    }

    fun onDisconnect(call: Call) {
        Timber.e("onDisconnect Call: $call")
        telnyxCall.endCall(call.callId)
        /*CometChat.rejectCall(call.sessionId, CometChatConstants.CALL_STATUS_CANCELLED,
            object : CometChat.CallbackListener<Call>() {
                override fun onSuccess(p0: Call?) {
                    Timber.e("onSuccess: reject")
                }

                override fun onError(p0: CometChatException?) {
                    Toast.makeText(
                        connectionContext, "Unable to end call due to ${p0?.code}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })*/
    }

    override fun onAnswer() {
        telnyxCall.acceptCall(telnyxCall.callId, telnyxCall.callId.toString())
        destroyConnection()
        val acceptIntent =
            Intent(connectionContext, MainActivity::class.java)
        acceptIntent.putExtra("SESSIONID", telnyxCall.sessionId)
        acceptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        connectionContext.startActivity(acceptIntent)

        /*if (telnyxCall.sessionId != null) {
            CometChat.acceptCall(telnyxCall.sessionId, object : CallbackListener<Call>() {
                override fun onSuccess(call: Call) {
                    destroyConnection()
                    val acceptIntent =
                        Intent(connectionContext, CometChatStartCallActivity::class.java)
                    acceptIntent.putExtra(UIKitConstants.IntentStrings.SESSION_ID, call.sessionId)
                    acceptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    connectionContext.startActivity(acceptIntent)
                }

                override fun onError(e: CometChatException) {
                    destroyConnection()
                    Toast.makeText(
                        connectionContext,
                        "Call cannot be answered due to " + e.code,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }*/
    }

    fun destroyConnection() {
        setDisconnected(DisconnectCause(DisconnectCause.REMOTE, "Rejected"))
        Timber.e("destroyConnection")
        super.destroy()
    }

    override fun onReject() {
        Timber.e("onReject: ")
        telnyxCall.endCall(telnyxCall.callId)
        destroyConnection()
        /*if (telnyxCall.sessionId != null) {
            CometChat.rejectCall(
                telnyxCall.sessionId,
                CometChatConstants.CALL_STATUS_REJECTED,
                object : CallbackListener<Call?>() {
                    override fun onSuccess(call: Call?) {
                        Timber.e("onSuccess: reject")
                        destroyConnection()
                    }

                    override fun onError(e: CometChatException) {
                        destroyConnection()
                        Timber.e("onErrorReject: %s", e.message)
                        Toast.makeText(
                            connectionContext,
                            "Call cannot be rejected due to" + e.code,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }*/
    }

    fun onOutgoingReject() {
        Timber.e("onDisconnect")
        destroyConnection()
        setDisconnected(DisconnectCause(DisconnectCause.REMOTE, "REJECTED"))
    }
}