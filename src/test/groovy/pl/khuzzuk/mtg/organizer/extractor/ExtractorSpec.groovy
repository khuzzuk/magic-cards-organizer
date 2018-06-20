package pl.khuzzuk.mtg.organizer.extractor

import pl.khuzzuk.mtg.organizer.BusTest
import pl.khuzzuk.mtg.organizer.PropertyContainer
import pl.khuzzuk.mtg.organizer.model.ManaType
import pl.khuzzuk.mtg.organizer.model.Rarity
import pl.khuzzuk.mtg.organizer.model.card.BasicLandCard
import pl.khuzzuk.mtg.organizer.model.card.Card
import pl.khuzzuk.mtg.organizer.model.card.CreatureCard
import pl.khuzzuk.mtg.organizer.model.card.LandCard
import pl.khuzzuk.mtg.organizer.model.card.TransformableCreatureCard
import pl.khuzzuk.mtg.organizer.model.type.BasicType
import pl.khuzzuk.mtg.organizer.serialization.PredefinedSkillRepo
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicBoolean

import static java.util.concurrent.TimeUnit.SECONDS
import static org.awaitility.Awaitility.await
import static pl.khuzzuk.mtg.organizer.Event.*

class ExtractorSpec extends Specification implements BusTest {
    @Shared
    PropertyContainer<Card> card = new PropertyContainer<>()

    void setupSpec() {
        setupBus()
        new HtmlCardExtractor(bus).load()
        def repo = new PredefinedSkillRepo(bus)
        repo.load()
        AtomicBoolean finishedInitialization = new AtomicBoolean()
        bus.message(PREDEFINED_SKILLS).withContent(repo).onResponse({finishedInitialization.set(true)}).send()
        await().atMost(2, SECONDS).until({finishedInitialization.get()})
        subscribingFor(CARD_DATA).accept({ card.put(it as Card) }).subscribe()
    }

    void setup() {
        card.clear()
    }

    void closeSpec() {
        closeBus()
    }

    def 'convert plains Basic Land url to Card'() {
        given:
        def url = 'https://scryfall.com/card/md1/19'

        when:
        message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})

        then:
        def result = card.get() as BasicLandCard

        result.printRef == 'Modern Event Deck 2014 (MD1)'
        result.printOrder == 19

        //front
        result.rarity == Rarity.COMMON
        result.name == 'Plains'
        result.front.toString() == 'https://img.scryfall.com/cards/large/en/md1/19.jpg?1517813031'

        def type = result.type
        type.basicType == BasicType.BasicLand
        type.primaryTypes.size() == 1
        "Basic" in type.primaryTypes
        type.secondaryTypes.size() == 1
        "Plains" in type.secondaryTypes
        type.colors.size() == 1
        type.colors.get(0) == ManaType.WHITE
    }

    def 'convert forest Basic Land url to Card'() {
        given:
        def url = 'https://scryfall.com/card/soi/295'

        when:
        message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})

        then:
        def result = card.get() as BasicLandCard

        result.printRef == 'Shadows over Innistrad (SOI)'
        result.printOrder == 295

        //front
        result.rarity == Rarity.COMMON
        result.name == 'Forest'
        result.front.toString() == 'https://img.scryfall.com/cards/large/en/soi/295.jpg?1517813031'

        def type = result.type
        type.basicType == BasicType.BasicLand
        type.primaryTypes.size() == 1
        "Basic" in type.primaryTypes
        type.secondaryTypes.size() == 1
        "Forest" in type.secondaryTypes
        type.colors.size() == 1
        type.colors.get(0) == ManaType.GREEN
    }

    def 'convert island Basic Land url to Card'() {
        given:
        def url = 'https://scryfall.com/card/soi/286'

        when:
        message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})

        then:
        def result = card.get() as BasicLandCard

        result.printRef == 'Shadows over Innistrad (SOI)'
        result.printOrder == 286

        //front
        result.rarity == Rarity.COMMON
        result.name == 'Island'
        result.front.toString() == 'https://img.scryfall.com/cards/large/en/soi/286.jpg?1517813031'

        def type = result.type
        type.basicType == BasicType.BasicLand
        type.primaryTypes.size() == 1
        "Basic" in type.primaryTypes
        type.secondaryTypes.size() == 1
        "Island" in type.secondaryTypes
        type.colors.size() == 1
        type.colors.get(0) == ManaType.BLUE
    }

    def 'convert Land url to Card'() {
        given:
        def url = 'https://scryfall.com/card/md1/17'

        when:
        message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})

        then:
        def result = card.get() as LandCard

        result.printRef == 'Modern Event Deck 2014 (MD1)'
        result.printOrder == 17

        //front
        result.rarity == Rarity.RARE
        result.name == 'Vault of the Archangel'
        result.text == '“For centuries my creation kept this world in balance. Now only her shadow remains.” —Sorin Markov'
        result.front.toString() == 'https://img.scryfall.com/cards/large/en/md1/17.jpg?1517813031'
        result.skills.size() == 2
        result.skills.get(0).text == '{T}: Add {C}.'
        result.skills.get(1).text == '{2}{W}{B}, {T}: Creatures you control gain deathtouch and lifelink until end of turn.'

        def type = result.type
        type.basicType == BasicType.Land
        type.primaryTypes.size() == 0
        type.secondaryTypes.size() == 0
    }

    def 'convert Creature url to Card'() {
        given:
        def url = 'https://scryfall.com/card/ima/11'

        when:
        message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})

        then:
        def result = card.get() as CreatureCard

        result.printRef == 'Iconic Masters (IMA)'
        result.printOrder == 11

        //front
        result.rarity == Rarity.MYTHIC_RARE
        result.name == 'Avacyn, Angel of Hope'
        result.text == 'A golden helix streaked skyward from the Helvault. A thunderous explosion shattered the silver monolith and Avacyn emerged, free from her prison at last.'
        result.front.toString() == 'https://img.scryfall.com/cards/large/en/ima/11.jpg?1517813031'
        result.attack == 8
        result.defense == 8
        result.skills.size() == 4
        result.skills.get(0).text == 'Flying'
        result.skills.get(1).text == 'Vigilance'
        result.skills.get(2).text == 'Indestructible'
        result.skills.get(3).text == 'Other permanents you control have indestructible.'

        def type = result.type
        type.basicType == BasicType.Creature
        'Legendary' in type.primaryTypes
        'Angel' in type.secondaryTypes

        def manaCost = result.manaCost
        manaCost.generic.value == 5
        manaCost.white.value == 3
        manaCost.green.value == 0
        manaCost.blue.value == 0
        manaCost.red.value == 0
        manaCost.black.value == 0
        manaCost.colorless.value == 0
    }

    def 'convert Transformable Creature url to Card'() {
        given:
        def url = 'https://scryfall.com/card/soi/5'

        when:
        message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})

        then:
        def result = card.get() as TransformableCreatureCard

        result.printRef == 'Shadows over Innistrad (SOI)'
        result.printOrder == 5

        //front
        result.rarity == Rarity.MYTHIC_RARE
        result.name == 'Archangel Avacyn'
        result.text == '“Wings that once bore hope are now stained with blood. She is our guardian no longer.” —Grete, cathar apostate'
        result.front.toString() == 'https://img.scryfall.com/cards/large/en/soi/5a.jpg?1518204266'
        result.attack == 4
        result.defense == 4
        result.skills.size() == 5
        result.skills.get(0).text == 'Flash'
        result.skills.get(1).text == 'Flying'
        result.skills.get(2).text == 'Vigilance'
        result.skills.get(3).text == 'When Archangel Avacyn enters the battlefield, creatures you control gain indestructible until end of turn.'
        result.skills.get(4).text == 'When a non-Angel creature you control dies, transform Archangel Avacyn at the beginning of the next upkeep.'

        def type = result.type
        type.basicType == BasicType.TransformableCreature
        'Legendary' in type.primaryTypes
        'Angel' in type.secondaryTypes

        //back
        result.transformedName == 'Avacyn, the Purifier'
        result.back.toString() == 'https://img.scryfall.com/cards/large/en/soi/5b.jpg?1518204266'
        result.transformedAttack == 6
        result.transformedDefense == 5
        result.transformedSkills.size() == 2
        result.transformedSkills.get(0).text == 'Flying'
        result.transformedSkills.get(1).text == 'When this creature transforms into Avacyn, the Purifier, it deals 3 damage to each other creature and each opponent.'

        def transformedType = result.transformedType
        transformedType.basicType == BasicType.TransformableCreature
        'Legendary' in transformedType.primaryTypes
        'Angel' in transformedType.secondaryTypes

        def manaCost = result.manaCost
        manaCost.generic.value == 3
        manaCost.white.value == 2
        manaCost.green.value == 0
        manaCost.blue.value == 0
        manaCost.red.value == 0
        manaCost.black.value == 0
        manaCost.colorless.value == 0
    }
}
