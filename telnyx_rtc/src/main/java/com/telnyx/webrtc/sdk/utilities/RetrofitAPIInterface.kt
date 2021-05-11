/*
 * Copyright © 2021 Telnyx LLC. All rights reserved.
 */

package com.telnyx.webrtc.sdk.utilities

import RetrofitAPIClient
import com.telnyx.webrtc.sdk.verto.receive.FcmRegistrationResponse
import com.telnyx.webrtc.sdk.verto.receive.TelnyxNotificationServiceResponse
import com.telnyx.webrtc.sdk.verto.send.CreateCredential
import retrofit2.Call
import retrofit2.http.*

interface RetrofitAPIInterface {

    @POST("push_notifications/registration")
    @FormUrlEncoded
    fun registrationPost(
        @Field("action") action: String,
        @Field("type") type: String,
        @Field("device_id") device_id: String
    ): Call<FcmRegistrationResponse>


   /* @POST("credentials")
    @FormUrlEncoded
    fun createCredentials(@Field("type") type: String,
                          @Field("data") data: JSONObject
    ): retrofit2.Call<TelnyxNotificationServiceResponse> */

    @POST("credentials")
    @Headers( "accept: application/json", "cache-control: no-cache", "content-type: application/json" )
    fun createCredentials(@Body createCredential: CreateCredential?): Call<TelnyxNotificationServiceResponse>?
}

object ApiUtils {
    val BASE_URL = "http://push-notification.query.consul:2023/v2/"
    val apiService: RetrofitAPIInterface
        get() = RetrofitAPIClient.getClient(BASE_URL)!!.create(RetrofitAPIInterface::class.java)
}