package com.vattenfall.emobility.infrastructure.clock

import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

/**
 * Since there is just one bean for the whole Spring Context, one thread/test moving it will impact other threads/tests.
 * Therefore we only allow to move time forward, and you should only depend on the ORDER, and not on a specific time itself
 * The only thing this clock guarantees, is that the time moves forward.
 * Also, each read from this clock moves the time forward, to make all values unique (helps in testing).
 */
internal class ProgressingClock : CurrentClock {
    private val currentInstant = AtomicLong()

    override fun invoke(): Instant {
        val laterTime = currentInstant.addAndGet(1)
        return Instant.ofEpochSecond(laterTime)
    }

    fun moveForward(numberOfSeconds: Long) {
        if(numberOfSeconds < 0) throw IllegalArgumentException("You can move the clock only forward. Received seconds: $numberOfSeconds")
        currentInstant.addAndGet(numberOfSeconds)
    }
}
