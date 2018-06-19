package pl.khuzzuk.mtg.organizer.settings;

import lombok.RequiredArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;

@RequiredArgsConstructor
public class SettingsManager implements Loadable {
    private final Bus<Event> bus;
    private SettingsData data;

    @Override
    public void load() {
        data = new SettingsData();
    }
}
