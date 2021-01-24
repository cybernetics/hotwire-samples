package com.grahamis.hotwire.service

import kotlinx.coroutines.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.IOException
import java.net.Socket
import java.net.SocketAddress
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExtendWith(SpringExtension::class)
class PingServiceTest {
    @Mock
    private lateinit var socket: Socket

    private val hostname = "localhost"
    private val port = 0

    @Test
    fun `should ping`() {
        mockSocketConnects()
        Assertions.assertThat(
            runBlocking { PingService(socket).ping(hostname, port) }
        ).isGreaterThanOrEqualTo(0)
    }

    @Test
    fun `should timeout`() {
        mockSocketTimeout()
        Assertions.assertThat(
            runBlocking { PingService(socket).ping(hostname, port) }
        ).isEqualTo(-1)
    }

    private fun mockSocketTimeout() {
        `when`(socket.connect(any(SocketAddress::class.java))).thenThrow(IOException())
        `when`(socket.connect(any(SocketAddress::class.java), anyInt())).thenThrow(IOException())
    }

    private fun mockSocketConnects() {
        doNothing().`when`(socket).connect(any(SocketAddress::class.java))
        doNothing().`when`(socket).connect(any(SocketAddress::class.java), anyInt())
    }
}
