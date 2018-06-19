package pl.khuzzuk.mtg.organizer.extractor

import pl.khuzzuk.mtg.organizer.BusTest
import pl.khuzzuk.mtg.organizer.PropertyContainer
import pl.khuzzuk.mtg.organizer.model.Rarity
import pl.khuzzuk.mtg.organizer.model.card.Card
import pl.khuzzuk.mtg.organizer.model.card.TransformableCreatureCard
import pl.khuzzuk.mtg.organizer.model.type.BasicType
import pl.khuzzuk.mtg.organizer.serialization.PredefinedSkillRepo
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicBoolean

import static java.util.concurrent.TimeUnit.SECONDS
import static org.awaitility.Awaitility.await
import static pl.khuzzuk.mtg.organizer.Event.*

class ExtractorSpec extends Specification implements BusTest {
    PropertyContainer<Card> card = new PropertyContainer<>()

    void setupSpec() {
        setupBus()
        new HtmlCardExtractor(bus).load()
        def repo = new PredefinedSkillRepo(bus)
        repo.load()
        AtomicBoolean finishedInitialization = new AtomicBoolean()
        bus.message(PREDEFINED_SKILLS).withContent(repo).onResponse({finishedInitialization.set(true)}).send()
        await().atMost(2, SECONDS).until({finishedInitialization.get()})
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
