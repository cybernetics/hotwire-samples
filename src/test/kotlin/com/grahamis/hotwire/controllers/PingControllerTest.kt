package com.grahamis.hotwire.controllers

import com.grahamis.CustomMediaType
import com.grahamis.hasElement
import com.grahamis.matches
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress

@ExtendWith(SpringExtension::class)
@WebFluxTest(PingController::class)
class PingControllerTest {
    @Autowired
    private lateinit var webClient: WebTestClient

    @MockBean
    private lateinit var socket: Socket

    private val path = "/pinger"

    @Test
    fun shouldReturnTurboStreamContentType() {
        mockSocketTimeout()
        response().expectHeader().contentType(CustomMediaType.TURBO_STREAM)
    }

    @Test
    fun shouldReturnTurboStreamElement() {
        mockSocketTimeout()
        response().expectBody().hasElement("turbo-stream")
    }

    @Test
    fun shouldContainTimeout() {
        mockSocketTimeout()
        response().expectBody().xpath("//turbo-stream/template/li").isEqualTo("timeout")
    }

    @Test
    fun shouldContainSomeTime() {
        val response = response()
        response.expectBody().xpath("//turbo-stream/template/li").matches("[0-9]+ ms")
    }

    private fun mockSocketTimeout() {
        `when`(socket.connect(any(SocketAddress::class.java))).thenThrow(IOException())
        `when`(socket.connect(any(SocketAddress::class.java), anyInt())).thenThrow(IOException())
    }

    private fun response() =
        webClient.post().uri(path).accept(CustomMediaType.TURBO_STREAM, MediaType.TEXT_HTML).exchange()
}
