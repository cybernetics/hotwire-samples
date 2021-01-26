package com.grahamis.hotwire.service

import com.grahamis.hotwire.domain.SystemLoadAverage
import kotlinx.coroutines.*
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.lang.management.ManagementFactory
import java.lang.management.OperatingSystemMXBean
import java.time.*

@Service
class LoadService {
    private val osMxBean: OperatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean()

    @TestOnly
    fun streamBlocking() = runBlocking {
        stream()
    }

    suspend fun stream() =
        Flux.interval(Duration.ofSeconds(3)).map { SystemLoadAverage(osMxBean.systemLoadAverage) }
}
