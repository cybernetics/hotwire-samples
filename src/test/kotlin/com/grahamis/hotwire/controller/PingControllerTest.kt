package com.grahamis.hotwire.controller

import com.grahamis.CustomMediaType
import com.grahamis.hasElement
import com.grahamis.hotwire.config.ThymeleafConfig
import com.grahamis.hotwire.service.PingService
import com.grahamis.matches
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.MockReset
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExtendWith(SpringExtension::class)
@Import(ThymeleafConfig::class)
@WebFluxTest(PingController::class)
class PingControllerTest {
    @Autowired
    private lateinit var webClient: WebTestClient

    @MockBean(reset = MockReset.BEFORE)
    private lateinit var socket: Socket

    @MockBean(reset = MockReset.BEFORE)
    private lateinit var address: InetSocketAddress

    @SpyBean(PingService::class)
    private lateinit var pingService: PingService

    private val path = "/pinger"

    @Test
    fun `should respond with a Turbo Stream ContentType`() {
        mockSocketTimeout()
        response().expectHeader().contentType(CustomMediaType.TURBO_STREAM)
    }

    @Test
    fun `should return a Turbo Stream element`() {
        mockSocketTimeout()
        response().expectBody().hasElement("turbo-stream")
    }

    @Test
    fun `should contain timeout`() {
        mockSocketTimeout()
        response().expectBody().xpath("//turbo-stream/template/li").isEqualTo("timeout")
    }

    @Test
    fun `should contain some time`() {
        mockSocketConnects()
        response().expectBody().xpath("//turbo-stream/template/li").matches("[0-9]+ ms")
    }

    private fun mockSocketTimeout() {
        `when`(socket.connect(any(SocketAddress::class.java))).thenThrow(IOException())
        `when`(socket.connect(any(SocketAddress::class.java), anyInt())).thenThrow(IOException())
    }

    private fun mockSocketConnects() {
        doNothing().`when`(socket).connect(any(SocketAddress::class.java))
        doNothing().`when`(socket).connect(any(SocketAddress::class.java), anyInt())
    }

    private fun response() =
        webClient.post().uri(path).accept(CustomMediaType.TURBO_STREAM, MediaType.TEXT_HTML).exchange()
}
