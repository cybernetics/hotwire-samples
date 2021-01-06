package com.grahamis.hotwire.controllers

import com.grahamis.CustomMediaType
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import reactor.core.publisher.Mono
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


@RequestMapping("/pinger")
@Controller
class PingController {
    @Value("\${ping.hostname:127.0.0.1}")
    private val hostname: String = "127.0.0.1"

    @Value("\${ping.port:8080}")
    private val port: Int = 8080

    // Test will provide a `@MockBean`, otherwise create `Socket` when calling `ping(Socket)`
    @Autowired(required = false)
    private var socket: Socket? = null

    @ExperimentalTime
    @PostMapping(produces = [CustomMediaType.TURBO_STREAM_VALUE, MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun pinger(): Mono<ResponseEntity<String>> {
        val headers = HttpHeaders()

        headers.contentType = CustomMediaType.TURBO_STREAM

        // socket will be a `@MockBean` during testing, null otherwise
        return ping(socket ?: Socket()).map { duration ->
            val pingTime = if (duration < 0) "timeout" else "$duration ms"
            ResponseEntity
                .ok()
                .contentType(CustomMediaType.TURBO_STREAM)
                .body(
                    """
<turbo-stream action="append" target="pings">
  <template>
    <li>$pingTime</li>
  </template>
</turbo-stream>
                """.trimIndent()
                )
        }
    }

    @ExperimentalTime
    fun ping(socket: Socket): Mono<Long> = Mono.create {
        try {
            it.success(measureTime {
                socket.connect(InetSocketAddress(hostname, port), 10)
                if (hostname == "127.0.0.1") {
                    val sleep = Random.nextLong().absoluteValue % 10
                    if (sleep % 5 == 0L) throw IOException() // force some timeouts now and then
                    Thread.sleep(sleep)
                }
            }.toLongMilliseconds())
        } catch (e: IOException) {
            it.success(-1)
        }
    }
}
