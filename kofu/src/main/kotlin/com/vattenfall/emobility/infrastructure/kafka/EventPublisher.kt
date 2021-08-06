package com.vattenfall.emobility.infrastructure.kafka

/**
 * Generic interface for classes publishing to Kafka, so that they can be replaced in unit tests by a mock
 */
interface EventPublisher<T> {
    fun publish(event: T)
}
