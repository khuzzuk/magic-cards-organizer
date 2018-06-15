package pl.khuzzuk.mtg.organizer.extractor

import pl.khuzzuk.mtg.organizer.BusTest
import pl.khuzzuk.mtg.organizer.PropertyContainer
import pl.khuzzuk.mtg.organizer.model.Card
import pl.khuzzuk.mtg.organizer.model.CreatureCard
import pl.khuzzuk.mtg.organizer.model.type.BasicType
import spock.lang.Specification

import static java.util.concurrent.TimeUnit.SECONDS
import static org.awaitility.Awaitility.await
import static pl.khuzzuk.mtg.organizer.Event.CARD_DATA
import static pl.khuzzuk.mtg.organizer.Event.CARD_FROM_URL

class ExtractorSpec extends Specification implements BusTest {
    PropertyContainer<Card> card = new PropertyContainer<>()

    void setupSpec() {
        setupBus()
        new HtmlCardExtractor(bus).load()
    }

    void setup() {
        card.clear()
    }

    void closeSpec() {
        closeBus()
    }

    def 'convert Legendary Creature url to Card'() {
        given:
        def url = 'https://scryfall.com/card/soi/5'

        when:
        subscribingFor(CARD_DATA).accept({ card.put(it as Card) }).subscribe()
        message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})

        then:
        def result = card.get() as CreatureCard
        result.name == 'Archangel Avacyn'
        result.text == '“Wings that once bore hope are now stained with blood. She is our guardian no longer.” —Grete, cathar apostate'
        result.front.toString() == 'https://img.scryfall.com/cards/large/en/soi/5a.jpg?1518204266'
        result.attack == 4
        result.defense == 4
        def manaCost = result.manaCost
        manaCost.generic.value == 3
        manaCost.white.value == 2
        manaCost.green.value == 0
        manaCost.blue.value == 0
        manaCost.red.value == 0
        manaCost.black.value == 0
        manaCost.colorless.value == 0
        def type = result.type
        type.basicType == BasicType.Creature
        'Legendary' in type.primaryTypes
        'Angel' in type.secondaryTypes
    }
}
