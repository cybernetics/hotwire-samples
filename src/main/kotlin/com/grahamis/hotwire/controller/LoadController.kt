package com.grahamis.hotwire.controller

import com.grahamis.hotwire.service.LoadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import reactor.core.publisher.Flux

@RequestMapping("/load")
@Controller
class LoadController {
    @Autowired
    private lateinit var loadService: LoadService

    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @ResponseBody
    // TODO: investigate the difference in behaviour with Turbo Streams and Chrome browser support with no explicit Flux import and instead:
//    suspend fun load() = loadService.streamCoroutine()
    fun load(): Flux<String> = loadService.stream()
        .map {
            """
<turbo-stream action="replace" target="load">
  <template>
    <span id="load">$it</span>
  </template>
</turbo-stream>
            """.trimIndent()
        }
}
