package com.grahamis.hotwire.controllers

import com.grahamis.CustomMediaType
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


@RequestMapping("/pinger")
@Controller
@ExperimentalTime
class PingController {
    @Value("\${ping.hostname:127.0.0.1}")
    private val hostname: String = "127.0.0.1"

    @Value("\${ping.port:8080}")
    private val port: Int = 8080

    // Test will provide a `@MockBean`, otherwise create `Socket` when calling `ping(Socket)`
    @Autowired(required = false)
    private var socket: Socket? = null

    @PostMapping(produces = [CustomMediaType.TURBO_STREAM_VALUE])
    @ResponseBody
    suspend fun pingerStream(): ResponseEntity<String> = ResponseEntity
        .ok()
        .contentType(CustomMediaType.TURBO_STREAM)
        .body(
            """
<turbo-stream action="append" target="pings">
  <template>
    <li>${pingTime()}</li>
  </template>
</turbo-stream>
            """.trimIndent()
        )

    @RequestMapping(produces = [MediaType.TEXT_HTML_VALUE], method = [RequestMethod.GET, RequestMethod.POST])
    suspend fun pingerPage(model: Model): String {
        model.addAttribute("pingTime", pingTime())
        return "ping"
    }

    private suspend fun pingTime(): String {
        // socket will be a `@MockBean` during testing, null otherwise
        val duration = ping(socket ?: Socket())
        return if (duration < 0) "timeout" else "$duration ms"
    }

    suspend fun ping(socket: Socket): Long = withContext(Dispatchers.IO) {
        runCatching {
            measureTime {
                socket.use {
                    it.connect(InetSocketAddress(hostname, port))

                    /**
                     * The following is purely for some randomness to simulate ping times.
                     * The randomness doesn't occur for unit tests (as it uses a mocked Socket).
                     * None of this sort of code would appear in a real app.
                     */
                    if (it::class == Socket::class) {
                        val sleep = Random.nextLong().absoluteValue % 10
                        if (sleep % 5 == 0L)
                            throw IOException() // force some timeouts now and then
                        delay(sleep)
                    }
                }
            }.toLongMilliseconds()
        }.getOrDefault(-1)
    }
}
