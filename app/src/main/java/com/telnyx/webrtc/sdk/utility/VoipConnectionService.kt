package com.telnyx.webrtc.sdk.utility

import android.os.Build
import android.telecom.*
import android.widget.Toast
import com.telnyx.webrtc.sdk.CredentialConfig
import com.telnyx.webrtc.sdk.TelnyxClient
import timber.log.Timber

class VoipConnectionService : ConnectionService() {

    companion object {
        var conn : CallConnection? = null
        //var telnyxClient: TelnyxClient? = null
    }

    /*init {
        telnyxClient = TelnyxClient(applicationContext)
        telnyxClient?.connect()
        val config = CredentialConfig("Username", "Password", "Oliver", "+35387711111", null, null, null)
        telnyxClient?.credentialLogin(config)
    }*/

    override fun onCreateIncomingConnection(connectionManagerPhoneAccount: PhoneAccountHandle?, request: ConnectionRequest?): Connection {
        val bundle = request!!.extras
        val sessionID = bundle.getString("SESSIONID")
        val name = bundle.getString("NAME")

        //ToDo Open socket connection and connect
        val telnyxClient = TelnyxClient(applicationContext)

        telnyxClient.connect()
        val config = CredentialConfig("Username", "Password", "Oliver", "+35387711111", null, null, null)
        telnyxClient.credentialLogin(config)

        //Todo Do we need to listen for replies here first?

       val call = telnyxClient?.call
        conn = call?.let { it -> CallConnection(applicationContext, it, telnyxClient!!) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            conn?.connectionProperties = Connection.PROPERTY_SELF_MANAGED
        }
        conn?.setCallerDisplayName(name, TelecomManager.PRESENTATION_ALLOWED)
        conn?.setAddress(request.address, TelecomManager.PRESENTATION_ALLOWED)
        conn?.setInitializing()
        conn?.setActive()
        return conn!!
    }

    override fun onCreateIncomingConnectionFailed(connectionManagerPhoneAccount: PhoneAccountHandle?, request: ConnectionRequest?) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
        Timber.e("onCreateIncomingFailed: %s",request.toString())
        Toast.makeText(applicationContext,"onCreateIncomingConnectionFailed",Toast.LENGTH_LONG).show();
    }

    override fun onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount: PhoneAccountHandle?, request: ConnectionRequest?) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
        Timber.e("onCreateOutgoingFailed: %s",request.toString())
        Toast.makeText(applicationContext,"onCreateOutgoingConnectionFailed",Toast.LENGTH_LONG).show();
    }

    override fun onCreateOutgoingConnection(connectionManagerPhoneAccount: PhoneAccountHandle?, request: ConnectionRequest?): Connection {
        val bundle = request!!.extras
        val sessionID = bundle.getString("SESSIONID")
        val name = bundle.getString("NAME")
        Timber.e("onCreateOutgoingConn : %s","$bundle \n $sessionID $name")

        //ToDo Open socket connection and connect
        val telnyxClient = TelnyxClient(applicationContext)
        telnyxClient.connect()
        val config = CredentialConfig("Username", "Password", "Oliver", "+35387711111", null, null, null)
        telnyxClient.credentialLogin(config)

        //Todo Do we need to listen for replies here first?
        val call = telnyxClient.call
        conn = call?.let { it -> CallConnection(applicationContext, it, telnyxClient) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            conn?.connectionProperties = Connection.PROPERTY_SELF_MANAGED
        }
        conn?.setCallerDisplayName(name, TelecomManager.PRESENTATION_ALLOWED)
        conn?.setAddress(request.address, TelecomManager.PRESENTATION_ALLOWED)
        conn?.setInitializing()
        conn?.setActive()
        return conn!!
    }

}