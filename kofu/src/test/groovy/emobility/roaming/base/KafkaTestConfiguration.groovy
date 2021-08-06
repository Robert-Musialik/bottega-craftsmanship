package emobility.roaming.base

import com.vattenfall.emobility.example.ExampleEvent
import com.vattenfall.emobility.infrastructure.kafka.KafkaSerdeFactory
import groovy.transform.CompileStatic
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.testcontainers.containers.KafkaContainer

import java.util.concurrent.ThreadLocalRandom

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG

@TestConfiguration
class KafkaTestConfiguration {
    @Bean
    String kafkaBrokers() {
        KafkaContainer kafkaContainer = new KafkaContainer("5.3.1")
        kafkaContainer.start()
        return kafkaContainer.getBootstrapServers()
    }

    @ConditionalOnBean(StreamsBuilder)
    @Bean
    // TopologyTestDriver needs to be setup after all topics has been initialized
    TopologyTestDriver topologyTestDriver(StreamsBuilder streamsBuilder) {
        Properties props = new Properties()
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "testStreams")
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:123")
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0)
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100)
        props.put(StreamsConfig.STATE_DIR_CONFIG, "./target/kafka-logs/streams/kafka" + ThreadLocalRandom.current().nextLong(0l, Long.MAX_VALUE))
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class.getCanonicalName())
        return new TopologyTestDriver(streamsBuilder.build(), props)
    }

    @Bean
    KafkaAdmin admin(String kafkaBrokers) {
        Map<String, Object> configs = new HashMap<>()
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers)
        return new KafkaAdmin(configs)
    }

    @Bean
    TestConsumerCreator testConsumerBuilder(String kafkaBrokers, KafkaSerdeFactory kafkaSerdeFactory) {
        return new TestConsumerCreator(kafkaBrokers, kafkaSerdeFactory)
    }

    @Bean
    KafkaTemplate<String, ExampleEvent> exampleKafkaTemplate(String kafkaBrokers, KafkaSerdeFactory kafkaSerdeFactory) {
        return new KafkaTemplate<>(producerFactory(kafkaBrokers, kafkaSerdeFactory.serializerFor( ExampleEvent.class)), true);
    }

    static <V> ProducerFactory<String, V> producerFactory(String kafkaBrokers, Serializer<V> valueSerializer) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        configs.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        DefaultKafkaProducerFactory<String, V> producerFactory = new DefaultKafkaProducerFactory<>(configs);
        producerFactory.setValueSerializer(valueSerializer);
        return producerFactory;
    }

    @Bean
    NewTopic example1Topic() {
        return createNewTopicForTests(com.vattenfall.emobility.example.ExampleFacadeKt.EXAMPLE_TOPIC_NAME_1)
    }

    @Bean
    NewTopic example2Topic() {
        return createNewTopicForTests(com.vattenfall.emobility.example.ExampleFacadeKt.EXAMPLE_TOPIC_NAME_2)
    }

    private static NewTopic createNewTopicForTests(String topicName) {
        return new NewTopic(topicName, 1, (short) 1)
    }
}
