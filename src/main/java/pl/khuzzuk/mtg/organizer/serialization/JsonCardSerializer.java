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

import static pl.khuzzuk.mtg.organizer.events.Event.*;

@RequiredArgsConstructor
@Component
public class JsonCardSerializer implements InitializingBean {
    private final Bus<Event> bus;
    private final ObjectMapper objectMapper;
    private final SettingsService settingsService;

    @Override
    public void afterPropertiesSet() {
        bus.subscribingFor(Event.CARD_DATA).accept(this::serializeCard).subscribe();
    }

    private void serializeCard(Card card) {
        try {
            Path currentCardsPath = Paths.get(settingsService.getCardsPathSettings());
            Path cardDir = Paths.get(currentCardsPath.toString(), card.getPrintRef());
            if (!Files.exists(cardDir)) {
                Files.createDirectory(cardDir);
            }

            Path pngPath = Paths.get(cardDir.toString(), getFileName(card, ".png"));
            bus.message(DOWNLOAD_IMAGE).withContent(Pair.of(card.getFront(), pngPath)).send();
            card.setDownloadedFront(pngPath.toUri().toURL());

            Path artPath = Paths.get(cardDir.toString(), getFileName(card, "_art.jpg"));
            bus.message(DOWNLOAD_IMAGE).withContent(Pair.of(card.getArt(), artPath)).send();
            card.setDownloadedArt(artPath.toUri().toURL());

            if (card instanceof TransformableCreatureCard) {
                TransformableCreatureCard transformableCard = (TransformableCreatureCard) card;
                Path backPngPath = Paths.get(cardDir.toString(), getFileName(card, "_back.png"));
                bus.message(DOWNLOAD_IMAGE).withContent(Pair.of(transformableCard.getBack(), backPngPath)).send();
                transformableCard.setDownloadedBack(backPngPath.toUri().toURL());

                Path backArtPath = Paths.get(cardDir.toString(), getFileName(card, "_back_art.jpg"));
                bus.message(DOWNLOAD_IMAGE).withContent(Pair.of(transformableCard.getBackArt(), backArtPath)).send();
                transformableCard.setDownloadedBackArt(backArtPath.toUri().toURL());
            }

            Path jsonPath = Paths.get(cardDir.toString(), getFileName(card, ".json"));
            objectMapper.writeValue(jsonPath.toFile(), card);

        } catch (JsonProcessingException e) {
            bus.message(ERROR).withContent("Cannot serialize card to JSON").send();
        } catch (IOException e) {
            bus.message(ERROR).withContent(String.format("Exception when serializing cardData: %s, %s", card, e)).send();
        }
    }

    static String getFileName(Card card, String suffix) {
        return String.format("%04d_%s%s", card.getPrintOrder(), card.getName(), suffix);
    }
}
