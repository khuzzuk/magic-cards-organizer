package pl.khuzzuk.mtg.organizer.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.model.card.Card;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static pl.khuzzuk.mtg.organizer.events.Event.CARD_DATA;
import static pl.khuzzuk.mtg.organizer.events.Event.CARD_INDEX;
import static pl.khuzzuk.mtg.organizer.events.Event.ERROR;

@RequiredArgsConstructor
public class JsonRepoSerializer implements InitializingBean {
    private final Bus<Event> bus;
    private final Path repoFile;
    private final ObjectMapper objectMapper;
    @Getter(AccessLevel.PACKAGE)
    private CardsContainer cardsContainer;

    @Override
    public void afterPropertiesSet() {
        loadRepo();
        bus.subscribingFor(CARD_DATA).accept(this::saveCard).subscribe();
        bus.subscribingFor(CARD_INDEX).accept(this::saveCard).subscribe();
    }

    private void loadRepo() {
        try {
            if (Files.exists(repoFile)) {
                cardsContainer = objectMapper.readValue(repoFile.toFile(), CardsContainer.class);
            } else {
                cardsContainer = new CardsContainer();
                Files.createFile(repoFile);
            }
        } catch (IOException e) {
            bus.message(ERROR).withContent("Cannot initialize card repository").send();
        }
    }

    private synchronized void saveCard(Card card) {
        cardsContainer.getCards().add(card);
        syncRepo();
    }

    private synchronized void syncRepo() {
        try {
            Path tempRepoFile = repoFile.getParent().resolve("tempRepo.json");
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
