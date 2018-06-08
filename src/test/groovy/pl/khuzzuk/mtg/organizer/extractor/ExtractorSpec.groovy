package pl.khuzzuk.mtg.organizer.extractor

import pl.khuzzuk.mtg.organizer.BusTest
import pl.khuzzuk.mtg.organizer.model.Card
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicReference

import static java.util.concurrent.TimeUnit.SECONDS
import static org.awaitility.Awaitility.await
import static pl.khuzzuk.mtg.organizer.Event.CARD_DATA
import static pl.khuzzuk.mtg.organizer.Event.CARD_FROM_URL

class ExtractorSpec extends Specification implements BusTest {
    AtomicReference<Card> card = new AtomicReference<>()

    void setupSpec() {
        setupBus()
        new HtmlCardExtractor(bus).load()
    }

    void setup() {
        card.set(null)
    }

    def 'convert basic url to Card'() {
        given:
        def url = 'https://scryfall.com/search?q=%21%22Vault+of+the+Archangel%22&utm_source=mediawiki'
        subscribingFor(CARD_DATA).accept({card.set(it as Card)}).subscribe()
        message(CARD_FROM_URL).withContent(url).withResponse(CARD_DATA).send()
        await().atMost(3, SECONDS).until({card.get() != null})
        card.get().name == 'Vault of the Archangel'
        card.get().text == '“For centuries my creation kept this world in balance. Now only her shadow remains.” —Sorin Markov'
    }
}
