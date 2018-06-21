package pl.khuzzuk.mtg.organizer.extractor

import pl.khuzzuk.mtg.organizer.BusTest
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.PropertyContainer
import pl.khuzzuk.mtg.organizer.extractor.rest.ScryfallClient
import pl.khuzzuk.mtg.organizer.model.Rarity
import pl.khuzzuk.mtg.organizer.model.card.Card
import pl.khuzzuk.mtg.organizer.model.card.LandCard
import pl.khuzzuk.mtg.organizer.model.type.BasicType
import pl.khuzzuk.mtg.organizer.serialization.PredefinedSkillRepo
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

import static org.awaitility.Awaitility.await

class CardJSONConverterSpec extends Specification implements BusTest {
    @Shared
    PropertyContainer<Card> card = new PropertyContainer<>()

    void setupSpec() {
        setupBus()
        new ScryfallClient(bus).load()
        new CardJSONConverter(bus).load()
        def predefinedSkillRepo = new PredefinedSkillRepo(bus)
        predefinedSkillRepo.load()
        AtomicBoolean preparationFinished = new AtomicBoolean(false)
        bus.message(Event.PREDEFINED_SKILLS).withContent(predefinedSkillRepo).onResponse({preparationFinished.set(true)}).send()
        subscribingFor(Event.CARD_DATA).accept({ card.put(it as Card) }).subscribe()
        await().atMost(3, TimeUnit.SECONDS).until({preparationFinished.get()})
    }

    void setup() {
        card.clear()
    }

    void closeSpec() {
        closeBus()
    }

    def "check land card conversion"() {
        given:
        URL uri = new URL('https://api.scryfall.com/cards/md1/17?format=json&pretty=true')

        when:
        bus.message(Event.CARD_FROM_URL).withContent(uri).send()
        await().atMost(4, TimeUnit.SECONDS).until({card.hasValue()})
        LandCard result = card.get() as LandCard

        then:
        result.name == 'Vault of the Archangel'
        result.rarity == Rarity.RARE
        result.front.toString() == 'https://img.scryfall.com/cards/png/en/md1/17.png?1517813031'
        result.art.toString() == 'https://img.scryfall.com/cards/art_crop/en/md1/17.jpg?1517813031'
        result.source.toString() == 'https://api.scryfall.com/cards/md1/17'
        result.skills.size() == 2
        result.skills.get(0).text == '{T}: Add {C}.'
        result.skills.get(1).text == '{2}{W}{B}, {T}: Creatures you control gain deathtouch and lifelink until end of turn.'
        result.printRef == 'md1'
        result.printFullName == 'Modern Event Deck 2014'
        result.printOrder == 17

        def type = result.type
        type.basicType == BasicType.Land
        type.primaryTypes.size() == 0
        type.secondaryTypes.size() == 0
    }
}
