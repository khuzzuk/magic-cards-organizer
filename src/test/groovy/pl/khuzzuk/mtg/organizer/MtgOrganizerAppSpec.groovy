package pl.khuzzuk.mtg.organizer

import pl.khuzzuk.messaging.Bus

class MtgOrganizerAppSpec extends JavaFxSpecification {
    Bus<Event> bus
    void setup() {
        bus = Bus.initializeBus(Event.class)
        MtgOrganizerApp.createContainer(bus)
    }
}
