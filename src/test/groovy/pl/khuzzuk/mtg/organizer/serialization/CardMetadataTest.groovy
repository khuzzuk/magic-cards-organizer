package pl.khuzzuk.mtg.organizer.serialization

import org.awaitility.Awaitility
import pl.khuzzuk.mtg.organizer.model.card.Card

import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

trait CardMetadataTest implements ImageDownloaderTest {
    private static final int serializationWaitingTimeout = 10

    boolean isJsonMetadataPresent(Card card) {
        Awaitility.await().atMost(serializationWaitingTimeout, TimeUnit.SECONDS).until({
            checkJsonIndexFile(card)
        })
        true
    }

    private boolean checkJsonIndexFile(Card card) {
        Files.exists(Paths.get(settingsService.cardsPathSettings, card.printRef,
                JsonCardSerializer.getFileName(card, '.json')))
    }
}
