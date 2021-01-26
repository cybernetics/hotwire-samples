package com.grahamis.hotwire.controller

import com.grahamis.hotwire.domain.SystemLoadAverage
import com.grahamis.hotwire.service.LoadService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@ExtendWith(SpringExtension::class)
@WebFluxTest(LoadController::class)
class LoadControllerTest {
    @Autowired
    private lateinit var webClient: WebTestClient

    @SpyBean
    private lateinit var loadService: LoadService

    private val path = "/load"

    @Test
    fun `should respond with a SSE ContentType`() {
        `when`(loadService.streamBlocking()).thenReturn(SystemLoadAverage(0.0).toMono().toFlux())
        response().expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
    }

    private fun response() =
        webClient.get().uri(path).accept(MediaType.ALL).exchange()
}
