package pl.khuzzuk.mtg.organizer.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class SettingsService implements InitializingBean {
    private final Bus<Event> bus;
    private final ObjectMapper objectMapper;
    @Getter
    private SettingsData data;
    private File settingsFile;

    @Override
    public void afterPropertiesSet() {
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
