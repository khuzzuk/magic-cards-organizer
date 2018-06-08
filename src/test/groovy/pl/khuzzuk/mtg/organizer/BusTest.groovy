package pl.khuzzuk.mtg.organizer

import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.messaging.BusPublisher
import pl.khuzzuk.messaging.BusSubscriber

trait BusTest {
    static Bus<Event> bus

    void setupBus() {
        bus = Bus.initializeBus(Event.class, System.out, true)
    }

    BusSubscriber<Event> subscribingFor(Event event) {
        bus.subscribingFor(event)
    }

    BusPublisher<Event> message(Event event) {
        bus.message(event)
    }
}