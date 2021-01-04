package com.grahamis.hotwire.controllers

import com.grahamis.CustomMediaType
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket


private val address = InetAddress.getByName("1.1.1.1")

@RequestMapping("/pinger")
@Controller
class Pinger {
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
        val (reachable, time) = executeAndMeasureTimeMillis {
            try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(address, 443), 0)
                    true
                }
            } catch (_: IOException) {
                false
            }
        }
        return if (reachable) time else -1
    }

    private inline fun <R> executeAndMeasureTimeMillis(block: () -> R): Pair<R, Long> {
        val start = System.currentTimeMillis()
        val result = block()
        return result to (System.currentTimeMillis() - start)
    }
}
