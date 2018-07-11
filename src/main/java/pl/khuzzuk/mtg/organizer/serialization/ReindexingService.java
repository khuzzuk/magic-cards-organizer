package pl.khuzzuk.mtg.organizer.serialization;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;
import pl.khuzzuk.mtg.organizer.model.card.Card;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static pl.khuzzuk.mtg.organizer.events.Event.*;

@RequiredArgsConstructor
public class ReindexingService implements Loadable {
    private final Bus<Event> bus;
    private ObjectMapper objectMapper;

    @Override
    public void load() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        bus.subscribingFor(REINDEX_REPO).accept(this::requestReindex).subscribe();
    }

    private void requestReindex(Path cardsDirectory) {
        try (Stream<Path> pathStream = Files.walk(cardsDirectory)) {
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
