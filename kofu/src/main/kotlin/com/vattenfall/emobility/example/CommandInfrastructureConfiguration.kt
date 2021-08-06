package com.vattenfall.emobility.example

import com.fasterxml.jackson.databind.ObjectMapper
import com.vattenfall.emobility.infrastructure.clock.CurrentClock
import com.vattenfall.emobility.infrastructure.clock.ProgressingClock
import com.vattenfall.emobility.infrastructure.config.Profiles
import com.vattenfall.emobility.infrastructure.jackson.customObjectMapper
import com.vattenfall.emobility.infrastructure.kafka.KafkaSerdeFactory
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.support.beans
import org.springframework.http.MediaType
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ErrorHandler
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS
import org.springframework.web.servlet.function.router
import java.lang.Boolean
import java.lang.Exception
import java.time.Instant

const val KAFKA_LISTENER_CONCURRENCY_LEVEL = 1
const val KAFKA_GROUP_ID = "kofu-example"

internal fun exampleBeans() = beans {

    //simple bean registration. ref<T>() allows you to point to another bean
    bean {
        ExampleFacade(
                ref<ExampleRepository>(),
                ExampleEventKafkaPublisher(
                        ref<String>("kafkaBrokers"),
                        ref<KafkaSerdeFactory>()
                )
        )
    }


    //how you register beans based on profiles
    profile(Profiles.NOT_INTEGRATION_TEST) {
        bean {
            { Instant.now() } as CurrentClock
        }
    }

    profile(Profiles.INTEGRATION_TEST) {
        bean {
            ProgressingClock()
        }
    }

    //how you register beans based on properties and conditions
    environment(condition = { getProperty("kafka.enabled") == "true" }) {
        bean(EXAMPLE_EVENTS_FACTORY_NAME) {
            concurrentKafkaListenerContainerFactory(
                    ref<String>("kafkaBrokers"),
                    KafkaProperties(),
                    ref<KafkaSerdeFactory>().deserializerFor(ExampleEvent::class.java),
                    KAFKA_LISTENER_CONCURRENCY_LEVEL,
                    KafkaErrorHandlers.defaultErrorHandler(),
                    KAFKA_GROUP_ID
            )
        }

        bean<KafkaSerdeFactory>() {
            KafkaSerdeFactory(
                ref<ObjectMapper>()
            )
        }

        bean<ObjectMapper> {
            customObjectMapper
        }
    }

    //instead of controllers, you define request handlers
    bean {
        router {
            (accept(MediaType.APPLICATION_JSON) and "/example/").nest {
                POST("/date") {
                    handleExampleRequest(it)
                }
            }
        }
    }
}

fun <T> concurrentKafkaListenerContainerFactory(
        kafkaBrokers: String,
        kafkaProperties: KafkaProperties,
        valueDeserializer: Deserializer<T>,
        concurrencyLevel: Int,
        errorHandler: ErrorHandler,
        groupId: String)
        : ConcurrentKafkaListenerContainerFactory<String, T> {
    return kafkaListenerCotainerFactory(
        kafkaBrokers,
        kafkaProperties.buildConsumerProperties(),
        valueDeserializer,
        concurrencyLevel,
        errorHandler,
        groupId
    )
}

private fun <T> kafkaListenerCotainerFactory(
        kafkaBrokers: String, consumerProperties: MutableMap<String, Any>,
        valueDeserializer: Deserializer<T>, concurrencyLevel: Int,
        errorHandler: ErrorHandler,
        groupId: String)
        : ConcurrentKafkaListenerContainerFactory<String, T> {
    val factory = ConcurrentKafkaListenerContainerFactory<String, T>()
    factory.setConsumerFactory(consumerFactory(kafkaBrokers, consumerProperties, valueDeserializer, groupId))
    factory.setConcurrency(concurrencyLevel)
    factory.setErrorHandler(errorHandler)
    return factory
}

fun <T> consumerFactory(
        kafkaBrokers: String,
        consumerProperties: MutableMap<String, Any>,
        valueDeserializer: Deserializer<T>,
        groupId: String)
        : ConsumerFactory<String, T> {
    consumerProperties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaBrokers
    consumerProperties[ConsumerConfig.GROUP_ID_CONFIG] = groupId
    consumerProperties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
    consumerProperties[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = Boolean.TRUE
    consumerProperties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
    consumerProperties[KEY_DESERIALIZER_CLASS] = StringDeserializer::class.java.name
    // due to spring-kafka bug (https://github.com/spring-projects/spring-kafka/issues/926)
    // value deserializer has to be configured twice (in props and set on factory below)
    consumerProperties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
    consumerProperties[VALUE_DESERIALIZER_CLASS] = valueDeserializer.javaClass.name
    val factory = DefaultKafkaConsumerFactory<String, T>(consumerProperties)
    factory.setValueDeserializer(ErrorHandlingDeserializer(valueDeserializer))
    return factory
}

private val logger = KotlinLogging.logger {}

object KafkaErrorHandlers {
    fun defaultErrorHandler(): ErrorHandler {
        return ErrorHandler { thrownException: Exception, record: ConsumerRecord<*, *>? ->
            if (record == null) {
                logger.info {"Unable to process event: ${thrownException.message}" }
            } else {
                logger.info {
                    "Unable to process event from topic: ${record.topic()} with key: ${record.key()} and body: ${record.value()}. Exception: ${thrownException.message}"
                }
            }
        }
    }
}
