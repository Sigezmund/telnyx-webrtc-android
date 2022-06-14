package com.telnyx.webrtc.sdk.utility

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.telnyx.webrtc.sdk.Call
import com.telnyx.webrtc.sdk.model.PushMetaData

class CallHandler(context: Context) {
    var callManagerContext = context
    var telecomManager : TelecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    lateinit var phoneAccountHandle: PhoneAccountHandle

    fun init() {
        val componentName = ComponentName(callManagerContext, VoipConnectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            phoneAccountHandle = PhoneAccountHandle(componentName, "VoIP Calling")
            val phoneAccount = PhoneAccount.builder(phoneAccountHandle, "VoIP Calling")
                .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER).build()
            telecomManager.registerPhoneAccount(phoneAccount)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun startOutgoingCall(call: Call) {
        val extras = Bundle()
        extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)
        val componentName = ComponentName(callManagerContext.packageName,
            VoipConnectionService::class.java.name)
        val phoneAccountHandle = PhoneAccountHandle(componentName, "estosConnectionServiceId")
        val test = Bundle()
        var number = "09999999999"
        extras.putString("NAME", "Name")
        extras.putString("SESSIONID", call.sessionId)

        test.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
        test.putParcelable(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, extras)
        try {
            if (callManagerContext.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS)
                == PackageManager.PERMISSION_GRANTED) {
                telecomManager.placeCall(Uri.parse("tel:$number"), test)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun startIncomingCall(pushMeta: PushMetaData) {

        //Create Call:


        if (callManagerContext.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS) ==
            PackageManager.PERMISSION_GRANTED) {
            val extras = Bundle()
            val uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, pushMeta.call_id.substring(0, 11),
                null)
            extras.putString("SESSIONID", pushMeta.call_id)
            extras.putString("NAME", pushMeta.caller_name)

            extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri)
            extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
            extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)
            var isCallPermitted = false
            isCallPermitted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telecomManager.isIncomingCallPermitted(phoneAccountHandle)
            } else {
                true
            }
            try {
                Log.e("startIncomingCall: ",extras.toString()+"\n"+isCallPermitted)
                telecomManager.addNewIncomingCall(phoneAccountHandle, extras)
            } catch (e: SecurityException) {
                val intent = Intent()
                intent.action = TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS
//                val componentName = ComponentName("com.android.server.telecom", "com.android.server.telecom.settings.EnableAccountPreferenceActivity")
//                intent.setComponent(componentName)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                callManagerContext.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(callManagerContext,"Error occured:"+e.message,Toast.LENGTH_LONG).show()
            }
        } else {
            Log.e("startIncomingCall: ","Permission not granted")
        }
    }
}