package pl.khuzzuk.mtg.organizer.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.initialize.Identification;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
@Identification(Event.SETTINGS_MANAGER)
public class SettingsService implements Loadable {
    private final Bus<Event> bus;
    private SettingsData data;
    private ObjectMapper objectMapper;
    private File settingsFile;

    @Override
    public void load() {
        objectMapper = new ObjectMapper();
        settingsFile = new File("app.json");
        data = new SettingsData();
        bus.subscribingFor(Event.SET_REPO_LOCATION).accept(this::setCardsPathSettings).subscribe();

        if (settingsFile.exists()) {
            loadSettings();
        }
    }

    public String getCardsPathSettings() {
        return data.getCardsRepoDirectory();
    }

    private void setCardsPathSettings(String newPath) {
        data.setCardsRepoDirectory(newPath);
        syncSettings();
    }

    private void loadSettings() {
        try {
            data = objectMapper.readValue(settingsFile, SettingsData.class);
        } catch (IOException e) {
            bus.message(Event.ERROR).withContent("Cannot read settings file").send();
        }
    }

    private synchronized void syncSettings() {
        try {
            objectMapper.writeValue(settingsFile, data);
        } catch (IOException e) {
            bus.message(Event.ERROR).withContent("Cannot save settings").send();
        }
    }
}
