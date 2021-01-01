package com.grahamis

import org.springframework.http.MediaType

class CustomMediaType(type: String) : MediaType(type) {
    companion object {
        val TURBO_STREAM = MediaType(TEXT_HTML, mapOf("turbo-stream" to "true"))
    }
}
