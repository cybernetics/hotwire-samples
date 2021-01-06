package com.grahamis

import org.springframework.http.MediaType

class CustomMediaType(type: String) : MediaType(type) {
    companion object {
        val TURBO_STREAM = MediaType(TEXT_HTML, mapOf("turbo-stream" to "*"))
        const val TURBO_STREAM_VALUE = "$TEXT_HTML_VALUE; turbo-stream=*"
//  TODO: remove above and uncomment below with Turbo 7 beta 3 https://github.com/hotwired/turbo/issues/24#issuecomment-754783881
//        val TURBO_STREAM = MediaType("application", "vnd.turbo.stream.html")
//        const val TURBO_STREAM_VALUE = "application/vnd.turbo.stream.html"
    }
}
