package emobility.roaming.example


import com.vattenfall.emobility.example.ExampleEntity
import com.vattenfall.emobility.example.ExampleEvent
import com.vattenfall.emobility.example.ExampleFacade
import emobility.roaming.base.IntegrationSpec
import emobility.roaming.base.TestConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate

import static emobility.roaming.base.pooling.PredefinedPollingConditions.WAIT
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DatabaseAndKafkaExampleIntegrationSpec extends IntegrationSpec {
    @Autowired ExampleFacade facade
    @Autowired KafkaTemplate<String, ExampleEvent> exampleKafkaTemplate
    TestConsumer<ExampleEvent> consumerOnTopic2

    def setup() {
        consumerOnTopic2 = testConsumerFactory.createTestConsumer(com.vattenfall.emobility.example.ExampleFacadeKt.EXAMPLE_TOPIC_NAME_2, ExampleEvent)
    }

    def "should save to and read from DB, with DB created using liquibase"() {
        given: "we have something to store"
            ExampleEntity exampleDto = new ExampleEntity(UUID.randomUUID().toString())

        when: "we ask a module to store something"
            facade.store(exampleDto)

        then: "module stores something"
            facade.searchFor(exampleDto.getId())
    }

    def "should send event to kafka and receive an event from kafka"() {
        given: "we have an event"
            ExampleEvent event = new ExampleEvent(UUID.randomUUID().toString())

        when: "we send an event to kafka"
            exampleKafkaTemplate.send(com.vattenfall.emobility.example.ExampleFacadeKt.EXAMPLE_TOPIC_NAME_1, event.id, event)

        then: "module passes this event to another topic"
            //always wait for outcomes, Kafka is a separate async process
            WAIT.eventually {
                ExampleEvent eventWithExpectedId = consumerOnTopic2.getEvents()
                        .collect { it.value() }
                        .find{it.id == event.id }
                assert eventWithExpectedId != null
            }
    }

    def "object mapper for http is configured for dates correctly"() {
        expect: "we send http post with custom json, module deserializes it correctly"
            mockMvc.perform(
                    post("/example/date")
                            .content("""
                                { 
                                    "id": "${UUID.randomUUID().toString()}", 
                                    "instant": "2019-07-03T10:15:30.00Z"  
                                }""")
                            .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isCreated())
    }
}
