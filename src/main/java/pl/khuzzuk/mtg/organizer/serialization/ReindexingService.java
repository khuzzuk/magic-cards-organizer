package pl.khuzzuk.mtg.organizer.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.model.card.Card;
import pl.khuzzuk.mtg.organizer.settings.SettingsService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static pl.khuzzuk.mtg.organizer.events.Event.*;

@RequiredArgsConstructor
@Component
public class ReindexingService implements InitializingBean {
    private final Bus<Event> bus;
    private final ObjectMapper objectMapper;
    private final SettingsService settingsService;

    @Override
    public void afterPropertiesSet() {
        bus.subscribingFor(SET_REPO_LOCATION).then(this::requestReindex).subscribe();
    }

    private void requestReindex() {
        try (Stream<Path> pathStream = Files.walk(Paths.get(settingsService.getCardsPathSettings()))) {
            pathStream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(this::reindexCard);
            bus.message(CARD_SETS).withResponse(REFRESH_CARD_SETS).send();
        } catch (IOException e) {
            bus.message(ERROR).withContent(String.format("Reindexing failed: %s", e.getMessage())).send();
        }
    }

    private void reindexCard(Path cardJsonLocation) {
        try {
            Card card = objectMapper.readValue(cardJsonLocation.toFile(), Card.class);
            bus.message(CARD_INDEX).withContent(card).send();
        } catch (IOException e) {
            bus.message(ERROR).withContent(String.format("Error during card indexing: %s", e.getMessage())).send();
        }
    }
}
