package com.telnyx.webrtc.sdk.socket

import android.Manifest
import android.content.Context
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.telnyx.webrtc.sdk.TelnyxClient
import com.telnyx.webrtc.sdk.testhelpers.BaseTest
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import androidx.test.rule.GrantPermissionRule
import com.telnyx.webrtc.sdk.utilities.ConnectivityHelper
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.mockito.Mockito
import org.robolectric.RuntimeEnvironment.application
import java.net.ConnectException
import kotlin.test.assertFailsWith

class TxSocketTest : BaseTest() {
    @Test
    fun mockFailure() = runBlocking {
        val mock = MockEngine { call ->
            respond(
                "{}",
                HttpStatusCode.OK,
                headersOf("Content-Type", ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mock) {
            install(WebSockets)
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }
        val resp = client.get<JsonObject>("dsf")
    }

    @MockK
    lateinit var connectivityHelper: ConnectivityHelper

    @MockK
    lateinit var connectivityManager: ConnectivityManager

    @MockK lateinit var activeNetwork: Network

    @MockK lateinit var capabilities: NetworkCapabilities

    @MockK lateinit var networkRequest: NetworkRequest

    @MockK
    lateinit var audioManager: AudioManager

    //TxSocketMocks
    @MockK
    private lateinit var listener: TelnyxClient

    @MockK
    private var mockContext: Context = mock(Context::class.java)

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        )

    @MockK
    private lateinit var socket: TxSocket

    @Before
    fun setup() {
        MockKAnnotations.init(this, true)
        Mockito.`when`(application.applicationContext).thenReturn(mockContext)

        every {socket.callOngoing()} just Runs
        every {socket.callNotOngoing()} just Runs

        every { mockContext.getSystemService(AppCompatActivity.AUDIO_SERVICE) } returns audioManager

        networkCallbackSetup()

    }

    private fun networkCallbackSetup() {
        var registered: Boolean? = null
        var available: Boolean? = null
        val callback = object : ConnectivityHelper.NetworkCallback() {
            override fun onNetworkAvailable() {
                available = true
            }

            override fun onNetworkUnavailable() {
                available = false
            }
        }
        mockkConstructor(NetworkRequest.Builder::class)
        mockkObject(NetworkRequest.Builder())
        val request = mockk<NetworkRequest>()
        every {
            anyConstructed<NetworkRequest.Builder>().addCapability(any()).addCapability(any())
                .build()
        } returns request
        every { mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.registerNetworkCallback(any(), callback) } just Runs
        every { connectivityManager.registerNetworkCallback(any(), callback) } answers { registered = true }
        every { connectivityManager.unregisterNetworkCallback(callback) } answers { registered = false }
        every { connectivityHelper.isNetworkEnabled(mockContext) } returns true
        every { connectivityHelper.registerNetworkStatusCallback(mockContext, callback) } just Runs

        every { mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every {connectivityManager.activeNetwork } returns activeNetwork
        every { connectivityHelper.isNetworkEnabled(mockContext) } returns false
        every { connectivityManager.getNetworkCapabilities(activeNetwork) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns false

        connectivityHelper.registerNetworkStatusCallback(mockContext, callback)
    }

    @Test
    fun `connect with valid host and port`() {
        socket = TxSocket(
            host_address = "rtc.telnyx.com",
            port = 14938,
        )
        listener = TelnyxClient(mockContext, socket)
        socket.connect(listener)
    }

    @Test
    fun `connect with empty host or port`() {
        socket = TxSocket(
            host_address = "",
            port = 0,
        )
        listener = TelnyxClient(mockContext, socket)
        assertFailsWith<ConnectException> {
            socket.connect(listener)
        }
    }

    @Test
    fun `set call to ongoing`() {
        socket = TxSocket(
            host_address = "rtc.telnyx.com",
            port = 14938,
        )
        socket.callOngoing()
        socket.ongoingCall shouldBe true
    }

    @Test
    fun `set call to not ongoing`() {
        socket = TxSocket(
            host_address = "rtc.telnyx.com",
            port = 14938,
        )
        socket.callNotOngoing()
        socket.ongoingCall shouldBe false
    }

    @Test
    fun `get web socket session before connection is established`() {
        assertFailsWith<UninitializedPropertyAccessException> {
            socket.getWebSocketSession()
        }
    }
}