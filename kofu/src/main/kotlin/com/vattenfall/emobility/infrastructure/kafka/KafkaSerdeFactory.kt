package com.vattenfall.emobility.infrastructure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

class KafkaSerdeFactory(
    private val objectMapper: ObjectMapper) {

    fun <T> serdeFor(aClass: Class<T>): Serde<T> {
        return Serdes.serdeFrom(serializerFor(aClass), deserializerFor(aClass))
    }

    fun <T> deserializerFor(aClass: Class<T>?): JsonDeserializer<T> {
        return JsonDeserializer(aClass, objectMapper)
    }

    fun <T> serializerFor(aClass: Class<T>): JsonSerializer<T> {
        val serializer = JsonSerializer<T>(objectMapper)
        // see: https://github.com/spring-projects/spring-kafka/issues/652
        serializer.isAddTypeInfo = false
        return serializer
    }
}
