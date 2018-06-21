package pl.khuzzuk.mtg.organizer.extractor

import pl.khuzzuk.mtg.organizer.BusTest
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.PropertyContainer
import pl.khuzzuk.mtg.organizer.extractor.rest.CardJSONConverter
import pl.khuzzuk.mtg.organizer.extractor.rest.ScryfallClient
import pl.khuzzuk.mtg.organizer.model.ManaCost
import pl.khuzzuk.mtg.organizer.model.ManaType
import pl.khuzzuk.mtg.organizer.model.Rarity
import pl.khuzzuk.mtg.organizer.model.card.Card
import pl.khuzzuk.mtg.organizer.model.card.CreatureCard
import pl.khuzzuk.mtg.organizer.model.card.LandCard
import pl.khuzzuk.mtg.organizer.model.type.BasicType
import pl.khuzzuk.mtg.organizer.serialization.PredefinedSkillRepo
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicBoolean

import static java.util.concurrent.TimeUnit.SECONDS
import static org.awaitility.Awaitility.await
import static pl.khuzzuk.mtg.organizer.Event.CARD_FROM_URL

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
        await().atMost(3, SECONDS).until({preparationFinished.get()})
    }

    void setup() {
        card.clear()
    }

    void closeSpec() {
        closeBus()
    }

    def 'convert plains basic land card'() {
        given:
        def url = new URL('https://api.scryfall.com/cards/md1/19?format=json&pretty=true')

        when:
        message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})
        def result = card.get() as LandCard

        then:
        result.rarity == Rarity.COMMON
        result.name == 'Plains'
        result.front.toString() == 'https://img.scryfall.com/cards/png/en/md1/19.png?1517813031'
        result.art.toString() == 'https://img.scryfall.com/cards/art_crop/en/md1/19.jpg?1517813031'
        result.source.toString() == 'https://api.scryfall.com/cards/md1/19'
        result.skills.size() == 1
        result.skills.get(0).text == '({T}: Add {W}.)'
        result.printRef == 'md1'
        result.printFullName == 'Modern Event Deck 2014'
        result.printOrder == 19
        result.edhrecRank == 0
        result.text == null

        def type = result.type
        type.basicType == BasicType.Land
        type.primaryTypes.size() == 1
        "Basic" in type.primaryTypes
        type.secondaryTypes.size() == 1
        "Plains" in type.secondaryTypes
        type.colors.size() == 1
        ManaType.WHITE in type.colors
    }

    def "convert land card"() {
        given:
        URL url = new URL('https://api.scryfall.com/cards/md1/17?format=json&pretty=true')

        when:
        bus.message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})
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
        result.edhrecRank == 542
        result.text == '"For centuries my creation kept this world in balance. Now only her shadow remains." —Sorin Markov'

        def type = result.type
        type.basicType == BasicType.Land
        type.primaryTypes.size() == 0
        type.secondaryTypes.size() == 0
        type.colors.size() == 2
        ManaType.WHITE in type.colors
        ManaType.BLACK in type.colors
    }

    def "convert creature card"() {
        given:
        URL url = new URL('https://api.scryfall.com/cards/ima/11?format=json&pretty=true')

        when:
        bus.message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})
        CreatureCard result = card.get() as CreatureCard

        then:
        result.name == 'Avacyn, Angel of Hope'
        result.rarity == Rarity.MYTHIC_RARE
        result.front.toString() == 'https://img.scryfall.com/cards/png/en/ima/11.png?1517813031'
        result.art.toString() == 'https://img.scryfall.com/cards/art_crop/en/ima/11.jpg?1517813031'
        result.source.toString() == 'https://api.scryfall.com/cards/ima/11'
        result.skills.size() == 4
        result.skills.get(0).text == 'Flying'
        result.skills.get(1).text == 'Vigilance'
        result.skills.get(2).text == 'Indestructible'
        result.skills.get(3).text == 'Other permanents you control have indestructible.'
        result.printRef == 'ima'
        result.printFullName == 'Iconic Masters'
        result.printOrder == 11
        result.edhrecRank == 811
        result.text == 'A golden helix streaked skyward from the Helvault. A thunderous explosion shattered the silver monolith and Avacyn emerged, free from her prison at last.'

        def type = result.type
        type.basicType == BasicType.Creature
        type.primaryTypes.size() == 1
        'Legendary' in type.primaryTypes
        type.secondaryTypes.size() == 1
        'Angel' in type.secondaryTypes
        type.colors.size() == 1
        ManaType.WHITE in type.colors

        ManaCost manaCost = result.manaCost
        manaCost.generic.value == 5
        manaCost.white.value == 3
        manaCost.green.value == 0
        manaCost.blue.value == 0
        manaCost.red.value == 0
        manaCost.black.value == 0
        manaCost.colorless.value == 0

        result.attack == 8
        result.defense == 8
    }
}
