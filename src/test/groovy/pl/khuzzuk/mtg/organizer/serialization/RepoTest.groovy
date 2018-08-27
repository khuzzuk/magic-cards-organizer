package pl.khuzzuk.mtg.organizer.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import pl.khuzzuk.mtg.organizer.model.card.Card

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

trait RepoTest {
    private static Path repoFile = Paths.get("testRepo.json")
    @Value('${serialization.repo.location}')
    Path repoFileFromSpring
    @Autowired
    ObjectMapper objectMapper
    @Autowired
    JsonRepoSerializer jsonRepoSerializer

    boolean checkIfCardIsOnDisk(Card... cards) {
        def repo = objectMapper.readValue(repoFileFromSpring.toFile(), CardsContainer)
        repo.cards.containsAll(cards) &&
                repo.cards.size() == cards.length
    }

    boolean checkIfCardIsInRepo(Card... cards) {
        jsonRepoSerializer.cardsContainer.cards.containsAll(cards) &&
                jsonRepoSerializer.cardsContainer.cards.size() == cards.length
    }

    void clearRepo() {
        try {
            jsonRepoSerializer.cardsContainer.cards.clear()
            Files.deleteIfExists(repoFileFromSpring)
            objectMapper.writeValue(repoFileFromSpring.toFile(), new CardsContainer())
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    void deleteRepo() {
        if (Files.exists(repoFile)) Files.delete(repoFile)
    }
}