package com.grahamis.hotwire.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.lang.management.ManagementFactory
import java.lang.management.OperatingSystemMXBean
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.*
import java.time.format.*

@Service
class LoadService {
    private val osMxBean: OperatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean()

    fun stream() = Flux.interval(Duration.ofSeconds(3)).map {
        "${now()}: ${systemLoadAverage()}"
    }

/*
    suspend fun streamCoroutine() = Flux.interval(Duration.ofSeconds(3)).map {
        "${now()}: ${systemLoadAverage()}"
    }
*/

    private fun systemLoadAverage(): String {
        val df = DecimalFormat("#.###")
        df.roundingMode = RoundingMode.CEILING
        return df.format(osMxBean.systemLoadAverage)
    }

    private fun now(): String {
        val current = OffsetDateTime.now(ZoneId.of("UTC"))
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return current.format(formatter)
    }
}
