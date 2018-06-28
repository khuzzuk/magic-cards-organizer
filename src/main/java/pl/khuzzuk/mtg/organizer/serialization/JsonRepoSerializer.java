package pl.khuzzuk.mtg.organizer.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.initialize.Identification;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;
import pl.khuzzuk.mtg.organizer.model.card.Card;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static pl.khuzzuk.mtg.organizer.Event.*;

@RequiredArgsConstructor
@Identification(JSON_REPO_SERIALIZER)
public class JsonRepoSerializer implements Loadable {
    private final Bus<Event> bus;
    private ObjectMapper objectMapper;
    @Getter(AccessLevel.PACKAGE)
    private CardsContainer cardsContainer;
    private Path repoFile;

    @Override
    public void load() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        objectMapper.enable(INDENT_OUTPUT);
        loadRepo();

        bus.subscribingFor(CARD_DATA).accept(this::saveCard).subscribe();
        bus.subscribingFor(CARD_INDEX).accept(this::saveCard).subscribe();
    }

    private void loadRepo() {
        try {
            repoFile = Paths.get("repo.json");
            if (Files.exists(repoFile)) {
                cardsContainer = objectMapper.readValue(repoFile.toFile(), CardsContainer.class);
            } else {
                cardsContainer = new CardsContainer();
                Files.createFile(repoFile);
            }
        } catch (IOException e) {
            bus.message(ERROR).withContent("Cannot initialize card repository");
        }
    }

    private synchronized void saveCard(Card card) {
        cardsContainer.getCards().add(card);
        syncRepo();
    }

    private synchronized void syncRepo() {
        try {
            Path tempRepoFile = Paths.get("tempRepo.json");
            if (Files.exists(tempRepoFile)) {
                bus.message(ERROR).withContent("index may be corrupted").send();
                return;
            }

            objectMapper.writeValue(tempRepoFile.toFile(), cardsContainer);

            Files.delete(repoFile);
            Files.move(tempRepoFile, repoFile);

        } catch (JsonProcessingException e) {
            bus.message(ERROR).withContent("Card format is not accepted by index").send();
        } catch (IOException e) {
            bus.message(ERROR).withContent(e.getMessage()).send();
        }
    }
}
