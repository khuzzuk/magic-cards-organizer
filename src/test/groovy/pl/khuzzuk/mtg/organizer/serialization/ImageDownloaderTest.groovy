package pl.khuzzuk.mtg.organizer.serialization

import org.springframework.beans.factory.annotation.Autowired
import pl.khuzzuk.mtg.organizer.model.card.Card
import pl.khuzzuk.mtg.organizer.model.card.TransformableCreatureCard
import pl.khuzzuk.mtg.organizer.settings.SettingsService

import java.nio.file.Files
import java.nio.file.Paths

trait ImageDownloaderTest {
    @Autowired
    SettingsService settingsService

    boolean checkImageForCard(Card card) {
        return checkFrontSideOfCard(card) && checkJsonIndexFile(card)
    }

    boolean checkImageForCard(TransformableCreatureCard card) {
        checkFrontSideOfCard(card) && checkBackSideOfCard(card) && checkJsonIndexFile(card)
    }

    private boolean checkFrontSideOfCard(Card card) {
        isFileOnDisk(card.downloadedFront) && isFileOnDisk(card.downloadedArt)
    }

    private boolean checkBackSideOfCard(TransformableCreatureCard card) {
        isFileOnDisk(card.downloadedBack) && isFileOnDisk(card.downloadedBackArt)
    }

    private boolean checkJsonIndexFile(Card card) {
        Files.exists(Paths.get(settingsService.cardsPathSettings, card.printRef,
                JsonCardSerializer.getFileName(card, '.json')))
    }

    void deleteTestFiles() {
        Files.walk(Paths.get("TestCards"))
                .sorted(Comparator.reverseOrder())
                .forEach({Files.delete(it)})
    }

    private static boolean isFileOnDisk(URL url) {
        Files.exists(Paths.get(url.getPath()))
    }
}
