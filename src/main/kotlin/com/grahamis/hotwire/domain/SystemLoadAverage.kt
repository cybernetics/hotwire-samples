package com.grahamis.hotwire.domain

import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.*
import java.time.format.*

class SystemLoadAverage(
    val value: Number,
    val timestamp: Instant = Instant.now()
) {
    private val valueFormatter = DecimalFormat("#.###")
    private val timestampFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    init {
        valueFormatter.roundingMode = RoundingMode.CEILING
    }

    fun avg(): String = valueFormatter.format(value)
    fun at(): String = timestamp.atZone(ZoneId.of("UTC")).format(timestampFormatter)
}

