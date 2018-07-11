package pl.khuzzuk.mtg.organizer.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;
import pl.khuzzuk.mtg.organizer.model.card.Card;
import pl.khuzzuk.mtg.organizer.model.card.TransformableCreatureCard;
import pl.khuzzuk.mtg.organizer.settings.SettingsService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.file.Paths.get;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static pl.khuzzuk.mtg.organizer.events.Event.*;

@RequiredArgsConstructor
public class JsonCardSerializer implements Loadable {
    private final Bus<Event> bus;
    private ObjectMapper objectMapper;
    private ImageDownloader imageDownloader;
    private AtomicReference<Path> cardsPath = new AtomicReference<>();
    private Runnable initialize;

    @Override
    public void load() {
        initialize = this::afterRepoSettings;

        bus.subscribingFor(SETTINGS_MANAGER).<SettingsService>accept(settings -> {
            if (isNotBlank(settings.getCardsPathSettings())) {
                cardsPath.set(get(settings.getCardsPathSettings()));
                initialize.run();
            }
        }).subscribe();

        bus.subscribingFor(SET_REPO_LOCATION).<String>accept(path -> {
            cardsPath.set(Paths.get(path));
            initialize.run();
            bus.message(REINDEX_REPO).withContent(cardsPath.get()).send();
        }).subscribe();
    }

    private synchronized void afterRepoSettings() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        imageDownloader = new ImageDownloader();

        bus.subscribingFor(Event.CARD_DATA).accept(this::serializeCard).subscribe();
        initialize = () -> {
        };
    }

    private void serializeCard(Card card) {
        try {
            Path currentCardsPath = cardsPath.get();
            Path cardDir = Paths.get(currentCardsPath.toString(), card.getPrintRef());
            if (!Files.exists(cardDir)) {
                Files.createDirectory(cardDir);
            }

            Path pngPath = Paths.get(cardDir.toString(), getFileName(card, ".png"));
            card.setFront(imageDownloader.downloadImage(card.getFront(), pngPath));
            Path artPath = Paths.get(cardDir.toString(), getFileName(card, "_art.jpg"));
            card.setFront(imageDownloader.downloadImage(card.getFront(), artPath));

            if (card instanceof TransformableCreatureCard) {
                TransformableCreatureCard transformableCard = (TransformableCreatureCard) card;
                Path backPngPath = Paths.get(cardDir.toString(), getFileName(card, "_back.png"));
                transformableCard.setBack(imageDownloader.downloadImage(transformableCard.getBack(), backPngPath));
                Path backArtPath = Paths.get(cardDir.toString(), getFileName(card, "_back_art.jpg"));
                transformableCard.setBackArt(imageDownloader.downloadImage(transformableCard.getBackArt(), backArtPath));
            }

            Path jsonPath = Paths.get(cardDir.toString(), getFileName(card, ".json"));
            objectMapper.writeValue(jsonPath.toFile(), card);

        } catch (JsonProcessingException e) {
            bus.message(ERROR).withContent("Cannot serialize card to JSON").send();
        } catch (ImageDownloader.DownloadException | IOException e) {
            bus.message(ERROR).withContent(e.getMessage()).send();
        }
    }

    private String getFileName(Card card, String suffix) {
        return String.format("%04d_%s%s", card.getPrintOrder(), card.getName(), suffix);
    }
}
