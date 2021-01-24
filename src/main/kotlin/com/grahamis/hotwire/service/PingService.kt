package com.grahamis.hotwire.service

import kotlinx.coroutines.*
import org.jetbrains.annotations.TestOnly
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@Service
class PingService() {
    // Test will provide a `@MockBean`
    @Autowired(required = false)
    private var socket: Socket? = null

    @TestOnly
    @Autowired(required = false)
    constructor(socket: Socket) : this() {
        this.socket = socket
    }

    @ExperimentalTime
    suspend fun ping(address: InetSocketAddress): Long = withContext(Dispatchers.IO) {
        val sock = socket ?: Socket()
        runCatching {
            measureTime {
                sock.use {
                    it.connect(address)
                    /**
                     * The following is purely for some randomness to simulate ping times.
                     * The randomness doesn't occur for unit tests (as it uses a mocked Socket).
                     * None of this sort of code would appear in a real app.
                     */
                    if (it::class == Socket::class) {
                        val sleep = Random.nextLong().absoluteValue % 10
                        if (sleep % 5 == 0L)
                            throw IOException() // force some timeouts now and then
                        delay(sleep)
                    }
                }
            }.toLongMilliseconds()
        }.getOrDefault(-1)
    }
}
