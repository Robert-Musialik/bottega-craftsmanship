package emobility.roaming.base

import com.vattenfall.emobility.infrastructure.kafka.EventPublisher

/**
 * Useful for unit tests with modules publishing to kafka
 */
class TestEventPublisher<T> implements EventPublisher<T> {
    private List<T> events = []

    @Override
    void publish(T event) {
        events.add(event)
    }

    def <Event> List<Event> filter(Class<Event> clazz) {
        List<Object> filteredEvents = events.findAll { clazz.isInstance(it) }
        return filteredEvents.collect { clazz.cast(it) }
    }
}
