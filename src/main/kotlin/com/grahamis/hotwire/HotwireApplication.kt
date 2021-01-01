package com.grahamis.hotwire

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HotwireApplication

fun main(args: Array<String>) {
    runApplication<HotwireApplication>(*args)
}
