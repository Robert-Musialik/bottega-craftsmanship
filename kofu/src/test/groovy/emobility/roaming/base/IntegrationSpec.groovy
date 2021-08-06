package emobility.roaming.base

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.vattenfall.emobility.AppRunner
import com.vattenfall.emobility.infrastructure.config.Profiles
import com.vattenfall.emobility.infrastructure.kafka.KafkaSerdeFactory
import groovy.transform.CompileStatic
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification

import java.time.Duration
import java.util.stream.StreamSupport

@SpringBootTest(classes = [AppRunner, KafkaTestConfiguration])
@ActiveProfiles([Profiles.INTEGRATION_TEST])
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@Testcontainers
abstract class IntegrationSpec extends Specification {
    @Autowired MockMvc mockMvc
    @Autowired WireMockServer wireMockServer
    @Autowired TestConsumerCreator testConsumerFactory
    @Autowired ObjectMapper objectMapper
}

class TestConsumerCreator {
    private String kafkaBrokers
    private KafkaSerdeFactory kafkaSerdeFactory

    public TestConsumerCreator(String kafkaBrokers, KafkaSerdeFactory kafkaSerdeFactory) {
        this.kafkaBrokers = kafkaBrokers
        this.kafkaSerdeFactory = kafkaSerdeFactory
    }

    /**
     * If you want to check a message on kafka, you can create a test consumer like so
     * Consumer<String, ExampleMessage> consumer = createTestConsumer(KafkaNames.EXAMPLE_TOPIC, ExampleMessage)
     **/
    public <T> TestConsumer<T> createTestConsumer(String topic, Class<T> clazz) {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(kafkaBrokers, UUID.randomUUID().toString(), "true")
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        ConsumerFactory<String, T> cf = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), kafkaSerdeFactory.deserializerFor(clazz))
        Consumer<String, T> consumer = cf.createConsumer()
        consumer.subscribe(List.of(topic))
        return new TestConsumer<>(consumer, topic)
    }
}

@CompileStatic
class TestConsumer<T> implements Consumer<String, T> {

    @Delegate
    private Consumer<String, T> delegate
    private String topic

    public TestConsumer(Consumer<String, T> delegate, String topic) {
        this.delegate = delegate
        this.topic = topic
    }

    /**
     * This lists all the messages in a batch which you should iterate over to find the one you need.
     * Two things to consider:
     * - a batch does not have to get all the events (depends on the batch size i kb, count, etc.) therefore it's better to set it in WAIT.eventually { ... }
     * - if the AUTO_OFFSET_RESET_CONFIG is not set to "earliest" you will only get messages after you pool (not after you subscribe), and even that comes with a delay
     */
    public List<ConsumerRecord<String, T>> getEvents(int timeout = 10) {
        ConsumerRecords<String, T> records = delegate.poll(Duration.ofSeconds(timeout))
        List<ConsumerRecord<String, T>> foundRecords = records.records(topic).asList()
        return foundRecords
    }

    /**
     * When you want to get the next message seen on any topic subscribed by this consumer.
     * Alternatively you can use KafkaTestUtils.getSingleRecord(consumer, topic, timout)
     * dsad
     */
    public Optional<T> getEvent(String id, int timeout = 10) {
        ConsumerRecords<String, T> records = delegate.poll(Duration.ofSeconds(timeout))
        Iterable<ConsumerRecord<String, T>> iterable = records.records(topic)
        return StreamSupport.stream(iterable.spliterator(), true)
                .filter { it.key() == id }
                .map { it.value() }
                .findFirst();
    }
}
