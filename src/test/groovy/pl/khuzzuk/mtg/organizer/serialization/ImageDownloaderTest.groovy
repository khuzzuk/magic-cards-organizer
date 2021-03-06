package pl.khuzzuk.mtg.organizer.serialization

import org.apache.commons.io.FileUtils
import org.awaitility.Awaitility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.FileSystemUtils
import pl.khuzzuk.mtg.organizer.model.card.Card
import pl.khuzzuk.mtg.organizer.model.card.TransformableCreatureCard
import pl.khuzzuk.mtg.organizer.settings.SettingsService

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

trait ImageDownloaderTest {
    private static final int downloadWaitingTimeout = 10
    private static final Path cardsDir = Paths.get("Test Cards")
    @Autowired
    SettingsService settingsService

    static void prepareCardDirectory() {
        if (!Files.exists(cardsDir)) {
            Files.createDirectory(cardsDir)
        }
    }

    boolean checkImageForCard(Card card) {
        return checkFrontSideOfCard(card)
    }

    boolean checkImageForCard(TransformableCreatureCard card) {
        checkFrontSideOfCard(card) && checkBackSideOfCard(card)
    }

    private boolean checkFrontSideOfCard(Card card) {
        Awaitility.await().atMost(downloadWaitingTimeout, TimeUnit.SECONDS).until({
            isFileOnDisk(card.downloadedFront) && isFileOnDisk(card.downloadedArt)
        })
        true
    }

    private boolean checkBackSideOfCard(TransformableCreatureCard card) {
        Awaitility.await().atMost(downloadWaitingTimeout, TimeUnit.SECONDS).until({
            isFileOnDisk(card.downloadedBack) && isFileOnDisk(card.downloadedBackArt)
        })
        true
    }

    void deleteTestFiles() {
        try {
            FileUtils.deleteDirectory(cardsDir.toFile())
        } catch (Exception e) {
            e.printStackTrace()
            FileSystemUtils.deleteRecursively(cardsDir)
        }
    }

    static boolean isFileOnDisk(URL url) {
        Files.exists(Paths.get(url.toURI()))
    }
}
