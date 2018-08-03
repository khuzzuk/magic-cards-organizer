package pl.khuzzuk.mtg.organizer

import javafx.util.Pair
import org.springframework.beans.factory.annotation.Autowired
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.messaging.BusSubscriber
import pl.khuzzuk.mtg.organizer.events.Event

import java.util.concurrent.TimeUnit

import static org.awaitility.Awaitility.await

trait BusTest {
    static boolean initialized
    static Bus<Event> bus
    static List<Pair<Event, PropertyContainer<?>>> listeners = new ArrayList<>()

    @Autowired
    void setBus(Bus<Event> bus) {
        if (!initialized) {
            initialized = true
            this.bus = bus
            listeners.forEach({ pair ->
                bus.subscribingFor(pair.key).accept({ pair.value.put(it) }).subscribe()
            })
        }
    }

    void closeBus() {
        bus.closeBus()
    }

    void busGetter(Event event, PropertyContainer<?> property) {
        listeners.add(new Pair<Event, PropertyContainer<?>>(event, property))
    }

    void checkProperty(PropertyContainer<?> propertyContainer, int timeout) {
        await().atMost(timeout, TimeUnit.SECONDS).until({propertyContainer.hasValue()})
    }

    BusSubscriber<Event> subscribingFor(Event event) {
        bus.subscribingFor(event)
    }
}