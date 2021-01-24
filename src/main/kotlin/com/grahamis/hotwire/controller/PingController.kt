package com.grahamis.hotwire.controller

import com.grahamis.CustomMediaType
import com.grahamis.hotwire.service.PingService
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
import java.net.InetSocketAddress
import kotlin.time.ExperimentalTime

@RequestMapping("/pinger")
@Controller
@ExperimentalTime
class PingController {
    @Autowired
    private lateinit var pingService: PingService

    @Value("\${ping.hostname:127.0.0.1}")
    private val hostname: String = "127.0.0.1"

    @Value("\${ping.port:8080}")
    private val port: Int = 8080

    // Test will provide a `@MockBean`
    @Autowired(required = false)
    private var address: InetSocketAddress? = null

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
        // socket and address will be a `@MockBean` during testing (null otherwise), so create real ones here
        val duration = pingService.ping(
            address ?: InetSocketAddress(hostname, port)
        )
        return if (duration < 0) "timeout" else "$duration ms"
    }
}
