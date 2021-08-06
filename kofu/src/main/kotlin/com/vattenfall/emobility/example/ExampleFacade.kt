package com.vattenfall.emobility.example

import com.vattenfall.emobility.infrastructure.kafka.KafkaSerdeFactory
import mu.KotlinLogging
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.support.BeanDefinitionDsl.BeanSupplierContext
import org.springframework.data.repository.Repository
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.net.URI
import java.time.Instant
import java.util.HashMap
import javax.persistence.Entity
import javax.persistence.Id

const val EXAMPLE_TOPIC_NAME_1 = "exampleTopic1"
const val EXAMPLE_TOPIC_NAME_2 = "exampleTopic2"
const val EXAMPLE_EVENTS_FACTORY_NAME = "exampleEventsFactory"

private val logger = KotlinLogging.logger {}

@Transactional
@KafkaListener(topics = [EXAMPLE_TOPIC_NAME_1], containerFactory = EXAMPLE_EVENTS_FACTORY_NAME)
open class ExampleFacade
    internal constructor(
            private val exampleRepository: ExampleRepository,
            private val kafkaPublisher: ExampleEventKafkaPublisher) {

    fun store(entity: ExampleEntity) {
        exampleRepository.save(entity)
    }

    fun searchFor(id: String): ExampleEntity? = exampleRepository.findById(id)

    @KafkaHandler
    fun receiveExampleEvent(exampleEvent: ExampleEvent) = kafkaPublisher.publish(exampleEvent)

    fun handleWebRequest(exampleEvent: ExampleEvent) {
        logger.info { "Web method deserialized object ok: $exampleEvent" }
    }
}

// DB access

@Entity(name = "example")
class ExampleEntity(
        @Id val id: String,
        val instant: Instant = Instant.now()) {

    constructor(id: String) : this(id, Instant.now())
}

internal interface ExampleRepository : Repository<ExampleEntity, String> {
    fun save(exampleEntity: ExampleEntity)
    fun findById(id: String): ExampleEntity?
}

// Kafka producer

data class ExampleEvent(
    val id: String
)

class ExampleEventKafkaPublisher (
        kafkaBrokers : String,
        kafkaSerdeFactory: KafkaSerdeFactory) {

    private val kafkaTemplate: KafkaTemplate<String, ExampleEvent> =
        KafkaTemplate(
            producerFactory<ExampleEvent>(
                kafkaBrokers,
                kafkaSerdeFactory.serializerFor(ExampleEvent::class.java)
            )
        )

    fun publish(event: ExampleEvent) {
        logger.info { "Publishing event with id: ${event.id} to kafka. Message body: $event" }
        kafkaTemplate.send(EXAMPLE_TOPIC_NAME_2, event.id, event)
    }

    fun <V> producerFactory(kafkaBrokers: String, valueSerializer: Serializer<V>): ProducerFactory<String, V> {
        val configs: MutableMap<String, Any> = HashMap()
        configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaBrokers
        configs[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        val producerFactory = DefaultKafkaProducerFactory<String, V>(configs)
        producerFactory.setValueSerializer(valueSerializer)
        return producerFactory
    }
}

// Web

internal fun BeanSupplierContext.handleExampleRequest(it: ServerRequest): ServerResponse {
    ref<ExampleFacade>()
            .handleWebRequest(
                    it.body(ExampleEvent::class.java))
    return ServerResponse.created(URI.create("/example")).build()
}
