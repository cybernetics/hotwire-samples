package com.grahamis.hotwire.controllers

import com.grahamis.CustomMediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket


@RequestMapping("/pinger")
@Controller
class PingController {
    @Value("\${ping.hostname:127.0.0.1}")
    private val hostname: String = "127.0.0.1"

    @Value("\${ping.port:8080}")
    private val port: Int = 8080

    // Test will provide a mock bean, otherwise ping() will self-create a Socket
    @Autowired(required = false)
    private var socket: Socket? = null

    @PostMapping(produces = ["${MediaType.TEXT_HTML_VALUE};turbo-stream", MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun pinger(): ResponseEntity<String> {
        val headers = HttpHeaders()

        headers.contentType = CustomMediaType.TURBO_STREAM

        val ping = ping()
        val pingTime = if (ping < 0) "timeout" else "$ping ms"

        return ResponseEntity
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

    fun ping(): Long {
        socket = socket ?: Socket()

        val (reachable, time) = executeAndMeasureTimeMillis {
            try {
                socket!!.use {
                    it.connect(InetSocketAddress(hostname, port), 1000)
                    true
                }
            } catch (_: IOException) {
                false
            }
        }

        /**
         * The socket has been closed and it can only be used once
         */
        socket = null

        return if (reachable) time else -1
    }

    private inline fun <R> executeAndMeasureTimeMillis(fn: () -> R): Pair<R, Long> {
        val start = System.currentTimeMillis()
        val result = fn()
        return result to (System.currentTimeMillis() - start)
    }
}
