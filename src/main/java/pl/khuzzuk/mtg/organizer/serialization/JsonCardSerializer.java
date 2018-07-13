package pl.khuzzuk.mtg.organizer.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.model.card.Card;
import pl.khuzzuk.mtg.organizer.model.card.TransformableCreatureCard;
import pl.khuzzuk.mtg.organizer.settings.SettingsService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

import static pl.khuzzuk.mtg.organizer.events.Event.*;

@RequiredArgsConstructor
@Component
public class JsonCardSerializer implements InitializingBean {
    private final Bus<Event> bus;
    private final ObjectMapper objectMapper;
    private final SettingsService settingsService;
    private AtomicReference<Path> cardsPath = new AtomicReference<>();

    @Override
    public void afterPropertiesSet() {
        cardsPath.set(Paths.get(settingsService.getData().getCardsRepoDirectory()));
        bus.subscribingFor(Event.CARD_DATA).accept(this::serializeCard).subscribe();

        bus.subscribingFor(SET_REPO_LOCATION).<String>accept(path -> {
            cardsPath.set(Paths.get(path));
            bus.message(REINDEX_REPO).withContent(cardsPath.get()).send();
        }).subscribe();
    }

    private void serializeCard(Card card) {
        try {
            Path currentCardsPath = cardsPath.get();
            Path cardDir = Paths.get(currentCardsPath.toString(), card.getPrintRef());
            if (!Files.exists(cardDir)) {
                Files.createDirectory(cardDir);
            }

            Path pngPath = Paths.get(cardDir.toString(), getFileName(card, ".png"));
            bus.message(DOWNLOAD_IMAGE).withContent(Pair.of(card.getFront(), pngPath)).send();
            card.setFront(pngPath.toUri().toURL());

            Path artPath = Paths.get(cardDir.toString(), getFileName(card, "_art.jpg"));
            bus.message(DOWNLOAD_IMAGE).withContent(Pair.of(card.getArt(), artPath)).send();
            card.setArt(artPath.toUri().toURL());

            if (card instanceof TransformableCreatureCard) {
                TransformableCreatureCard transformableCard = (TransformableCreatureCard) card;
                Path backPngPath = Paths.get(cardDir.toString(), getFileName(card, "_back.png"));
                bus.message(DOWNLOAD_IMAGE).withContent(Pair.of(transformableCard.getBack(), backPngPath)).send();
                transformableCard.setBack(backPngPath.toUri().toURL());

                Path backArtPath = Paths.get(cardDir.toString(), getFileName(card, "_back_art.jpg"));
                bus.message(DOWNLOAD_IMAGE).withContent(Pair.of(transformableCard.getBackArt(), backArtPath)).send();
                transformableCard.setBackArt(backArtPath.toUri().toURL());
            }

            Path jsonPath = Paths.get(cardDir.toString(), getFileName(card, ".json"));
            objectMapper.writeValue(jsonPath.toFile(), card);

        } catch (JsonProcessingException e) {
            bus.message(ERROR).withContent("Cannot serialize card to JSON").send();
        } catch (IOException e) {
            bus.message(ERROR).withContent(String.format("Exception when serializing cardData: %s, %s", card, e)).send();
        }
    }

    private String getFileName(Card card, String suffix) {
        return String.format("%04d_%s%s", card.getPrintOrder(), card.getName(), suffix);
    }
}
