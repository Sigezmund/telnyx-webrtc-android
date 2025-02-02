/*
 * Copyright © 2021 Telnyx LLC. All rights reserved.
 */

package com.telnyx.webrtc.sdk.verto.send

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.telnyx.webrtc.sdk.telnyx_rtc.BuildConfig
sealed class ParamRequest

data class LoginParam(
    @SerializedName("login_token")
    val loginToken: String?,
    val login: String?,
    val passwd: String?,
    val userVariables: JsonObject,
    val loginParams: Map<Any,Any>?,
    val sessid: String ,
) : ParamRequest()



data class CallParams(
    val sessid: String,
    val sdp: String,
    @SerializedName("User-Agent")
    val userAgent: String = "Android-" + BuildConfig.SDK_VERSION.toString(),
    val dialogParams: CallDialogParams
) : ParamRequest()

data class ByeParams(
    val sessid: String,
    val causeCode: Int,
    val cause: String,
    val dialogParams: ByeDialogParams
) : ParamRequest()


data class DisablePushParams(
    @SerializedName("user")
    val user: String,
    @SerializedName("User-Agent")
    val userVariables: UserVariables
) : ParamRequest()

data class TokenDisablePushParams(
    @SerializedName("login_token")
    val loginToken: String,
    @SerializedName("User-Agent")
    val userVariables: UserVariables
) : ParamRequest()

data class AttachCallParams(
    @SerializedName("userVariables")
    val userVariables: AttachUserVariables
) : ParamRequest()


data class AttachUserVariables(
    @SerializedName("push_notification_environment")
    val pushNotificationEnvironment:String = if (BuildConfig.DEBUG) "development" else "production",
    @SerializedName("push_notification_provider")
    val pushNotificationProvider:String = "android",
)

data class UserVariables(
    @SerializedName("push_device_token")
    val pushDeviceToken:String,
    @SerializedName("push_notification_provider")
    val pushNotificationProvider:String = "android",
)

data class ModifyParams(
    val sessid: String,
    val action: String,
    val dialogParams: CallDialogParams
) : ParamRequest()

data class InfoParams(
    val sessid: String,
    val dtmf: String,
    val dialogParams: CallDialogParams
) : ParamRequest()

data class StateParams(val state: String?) : ParamRequest()
