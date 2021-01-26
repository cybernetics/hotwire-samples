package com.grahamis.hotwire.controller

import com.grahamis.hotwire.domain.SystemLoadAverage
import com.grahamis.hotwire.service.LoadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux

@RequestMapping("/load")
@Controller
class LoadController {
    @Autowired
    private lateinit var loadService: LoadService

    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    suspend fun load(model: Model): String {
        model.addAttribute("loadStream", dataDrivenEach(loadService.stream()))
        return "load.turbo-stream"
    }

    private fun dataDrivenEach(stream: Flux<SystemLoadAverage>) =
        ReactiveDataDriverContextVariable(stream, 1)
}
