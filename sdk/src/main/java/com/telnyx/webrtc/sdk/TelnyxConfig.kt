package com.telnyx.webrtc.sdk

import com.telnyx.webrtc.sdk.model.LogLevel

sealed class TelnyxConfig

data class CredentialConfig(
    val sipUser: String,
    val sipPassword: String,
    val sipCallerIDName: String?,
    val sipCallerIDNumber: String?,
    val fcmDeviceId: String?,
    val ringtone: Int?,
    val ringBackTone: Int?,
    val logLevel: LogLevel = LogLevel.NONE
    ) : TelnyxConfig()

data class TokenConfig(
    val sipToken: String,
    val sipCallerIDName: String?,
    val sipCallerIDNumber: String?,
    val fcmDeviceId: String?,
    val ringtone: Int?,
    val ringBackTone: Int?,
    val logLevel: LogLevel = LogLevel.NONE
    ) : TelnyxConfig()

