package pl.khuzzuk.mtg.organizer.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import pl.khuzzuk.mtg.organizer.model.card.Card

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

trait RepoTest {
    @Value('${serialization.repo.location}')
    Path repoFile
    @Autowired
    ObjectMapper objectMapper
    @Autowired
    JsonRepoSerializer jsonRepoSerializer

    boolean checkIfCardIsOnDisk(Card... cards) {
        def repo = objectMapper.readValue(repoFile.toFile(), CardsContainer)
        repo.cards.containsAll(cards) &&
                repo.cards.size() == cards.length
    }

    boolean checkIfCardIsInRepo(Card... cards) {
        jsonRepoSerializer.cardsContainer.cards.containsAll(cards) &&
                jsonRepoSerializer.cardsContainer.cards.size() == cards.length
    }

    void clearRepo() {
        try {
            Files.deleteIfExists(repoFile)
            objectMapper.writeValue(repoFile.toFile(), new CardsContainer())
            jsonRepoSerializer.cardsContainer.cards.clear()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    void deleteRepo() {
        Files.delete(Paths.get("testRepo.json"))
    }
}