package com.grahamis

import org.springframework.http.MediaType

class CustomMediaType(type: String) : MediaType(type) {
    companion object {
        val TURBO_STREAM = MediaType("text", "vnd.turbo-stream.html")
        const val TURBO_STREAM_VALUE = "text/vnd.turbo-stream.html"
    }
}
