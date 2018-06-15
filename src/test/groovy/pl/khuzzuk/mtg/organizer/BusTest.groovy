package pl.khuzzuk.mtg.organizer

import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.messaging.BusPublisher
import pl.khuzzuk.messaging.BusSubscriber

import java.util.concurrent.TimeUnit

import static org.awaitility.Awaitility.await

trait BusTest {
    static Bus<Event> bus

    void setupBus() {
        bus = Bus.initializeBus(Event.class, System.out, true)
    }

    void closeBus() {
        bus.closeBus()
    }

    void busGetter(Event event, PropertyContainer<?> property) {
        bus.subscribingFor(event).accept({property.put(it)}).subscribe()
    }

    void checkProperty(PropertyContainer<?> propertyContainer) {
        await().atMost(1, TimeUnit.SECONDS).until({propertyContainer.hasValue()})
    }

    BusSubscriber<Event> subscribingFor(Event event) {
        bus.subscribingFor(event)
    }

    BusPublisher<Event> message(Event event) {
        bus.message(event)
    }
}