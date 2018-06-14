package pl.khuzzuk.mtg.organizer.settings

import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.initialize.Loadable

class SettingsManager implements Loadable {
    Bus<Event> bus
    SettingsData data

    SettingsManager(Bus<Event> bus) {
        this.bus = bus
    }

    @Override
    void load() {
        data = new SettingsData()

    }
}
