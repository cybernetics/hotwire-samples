package com.grahamis.hotwire.controller

import com.grahamis.CustomMediaType
import com.grahamis.hotwire.DefaultTemplateSelectorModifier
import com.grahamis.hotwire.TemplateSelectorModifier
import com.grahamis.hotwire.TurboStreamTemplateSelectorModifier
import com.grahamis.hotwire.service.PingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import kotlin.time.ExperimentalTime

@RequestMapping("/pinger")
@Controller
@ExperimentalTime
class PingController {
    @RequestMapping(produces = [CustomMediaType.TURBO_STREAM_VALUE])
    suspend fun pingerStream(model: Model) = view(model, TurboStreamTemplateSelectorModifier)

    @RequestMapping(produces = [MediaType.TEXT_HTML_VALUE])
    suspend fun pingerPage(model: Model) = view(model)

    private suspend fun view(
        model: Model,
        templateSelectorModifier: TemplateSelectorModifier = DefaultTemplateSelectorModifier
    ): String {
        model.addAttribute("pingTime", pingTime())
        return templateSelectorModifier.modifyName("ping")
    }

    @Autowired
    private lateinit var pingService: PingService

    @Value("\${ping.hostname:127.0.0.1}")
    private val hostname: String = "127.0.0.1"

    @Value("\${ping.port:8080}")
    private val port: Int = 8080

    private suspend fun pingTime(): String {
        val duration = pingService.ping(hostname, port)
        return if (duration < 0) "timeout" else "$duration ms"
    }
}
