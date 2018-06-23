package pl.khuzzuk.mtg.organizer.extractor

import pl.khuzzuk.mtg.organizer.BusTest
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.PropertyContainer
import pl.khuzzuk.mtg.organizer.extractor.rest.CardJSONConverter
import pl.khuzzuk.mtg.organizer.extractor.rest.ScryfallClient
import pl.khuzzuk.mtg.organizer.model.ManaCost
import pl.khuzzuk.mtg.organizer.model.ManaType
import pl.khuzzuk.mtg.organizer.model.Rarity
import pl.khuzzuk.mtg.organizer.model.card.ArtifactCard
import pl.khuzzuk.mtg.organizer.model.card.Card
import pl.khuzzuk.mtg.organizer.model.card.CreatureCard
import pl.khuzzuk.mtg.organizer.model.card.EnchantmentCard
import pl.khuzzuk.mtg.organizer.model.card.LandCard
import pl.khuzzuk.mtg.organizer.model.card.PlaneswalkerCard
import pl.khuzzuk.mtg.organizer.model.card.SorceryCard
import pl.khuzzuk.mtg.organizer.model.card.TransformableCreatureCard
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

    def 'convert forest basic land card'() {
        given:
        def url = new URL('https://api.scryfall.com/cards/bbd/254?format=json&pretty=true')

        when:
        message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})
        def result = card.get() as LandCard

        then:
        result.rarity == Rarity.COMMON
        result.name == 'Forest'
        result.front.toString() == 'https://img.scryfall.com/cards/png/en/bbd/254.png?1529063680'
        result.art.toString() == 'https://img.scryfall.com/cards/art_crop/en/bbd/254.jpg?1529063680'
        result.source.toString() == 'https://api.scryfall.com/cards/bbd/254'
        result.skills.size() == 1
        result.skills.get(0).text == '({T}: Add {G}.)'
        result.printRef == 'bbd'
        result.printFullName == 'Battlebond'
        result.printOrder == 254
        result.edhrecRank == 0
        result.text == null

        def type = result.type
        type.basicType == BasicType.Land
        type.primaryTypes.size() == 1
        "Basic" in type.primaryTypes
        type.secondaryTypes.size() == 1
        "Forest" in type.secondaryTypes
        type.colors.size() == 1
        ManaType.GREEN in type.colors
    }

    def 'convert island basic land card'() {
        given:
        def url = new URL('https://api.scryfall.com/cards/bbd/251?format=json&pretty=true')

        when:
        message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})
        def result = card.get() as LandCard

        then:
        result.rarity == Rarity.COMMON
        result.name == 'Island'
        result.front.toString() == 'https://img.scryfall.com/cards/png/en/bbd/251.png?1529063653'
        result.art.toString() == 'https://img.scryfall.com/cards/art_crop/en/bbd/251.jpg?1529063653'
        result.source.toString() == 'https://api.scryfall.com/cards/bbd/251'
        result.skills.size() == 1
        result.skills.get(0).text == '({T}: Add {U}.)'
        result.printRef == 'bbd'
        result.printFullName == 'Battlebond'
        result.printOrder == 251
        result.edhrecRank == 0
        result.text == null

        def type = result.type
        type.basicType == BasicType.Land
        type.primaryTypes.size() == 1
        "Basic" in type.primaryTypes
        type.secondaryTypes.size() == 1
        "Island" in type.secondaryTypes
        type.colors.size() == 1
        ManaType.BLUE in type.colors
    }

    def 'convert mountains basic land card'() {
        given:
        def url = new URL('https://api.scryfall.com/cards/bbd/253?format=json&pretty=true')

        when:
        message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})
        def result = card.get() as LandCard

        then:
        result.rarity == Rarity.COMMON
        result.name == 'Mountain'
        result.front.toString() == 'https://img.scryfall.com/cards/png/en/bbd/253.png?1529063672'
        result.art.toString() == 'https://img.scryfall.com/cards/art_crop/en/bbd/253.jpg?1529063672'
        result.source.toString() == 'https://api.scryfall.com/cards/bbd/253'
        result.skills.size() == 1
        result.skills.get(0).text == '({T}: Add {R}.)'
        result.printRef == 'bbd'
        result.printFullName == 'Battlebond'
        result.printOrder == 253
        result.edhrecRank == 0
        result.text == null

        def type = result.type
        type.basicType == BasicType.Land
        type.primaryTypes.size() == 1
        "Basic" in type.primaryTypes
        type.secondaryTypes.size() == 1
        "Mountain" in type.secondaryTypes
        type.colors.size() == 1
        ManaType.RED in type.colors
    }

    def 'convert swamp basic land card'() {
        given:
        def url = new URL('https://api.scryfall.com/cards/bbd/252?format=json&pretty=true')

        when:
        message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})
        def result = card.get() as LandCard

        then:
        result.rarity == Rarity.COMMON
        result.name == 'Swamp'
        result.front.toString() == 'https://img.scryfall.com/cards/png/en/bbd/252.png?1529063663'
        result.art.toString() == 'https://img.scryfall.com/cards/art_crop/en/bbd/252.jpg?1529063663'
        result.source.toString() == 'https://api.scryfall.com/cards/bbd/252'
        result.skills.size() == 1
        result.skills.get(0).text == '({T}: Add {B}.)'
        result.printRef == 'bbd'
        result.printFullName == 'Battlebond'
        result.printOrder == 252
        result.edhrecRank == 0
        result.text == null

        def type = result.type
        type.basicType == BasicType.Land
        type.primaryTypes.size() == 1
        "Basic" in type.primaryTypes
        type.secondaryTypes.size() == 1
        "Swamp" in type.secondaryTypes
        type.colors.size() == 1
        ManaType.BLACK in type.colors
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

    def "convert transformable creature card with rulings"() {
        given:
        URL url = new URL('https://api.scryfall.com/cards/soi/5?format=json&pretty=true')

        when:
        bus.message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})
        TransformableCreatureCard result = card.get() as TransformableCreatureCard

        then:
        result.name == 'Archangel Avacyn'
        result.rarity == Rarity.MYTHIC_RARE
        result.front.toString() == 'https://img.scryfall.com/cards/png/en/soi/5a.png?1518204266'
        result.art.toString() == 'https://img.scryfall.com/cards/art_crop/en/soi/5a.jpg?1518204266'
        result.source.toString() == 'https://api.scryfall.com/cards/soi/5'
        result.skills.size() == 5
        result.skills.get(0).text == 'Flash'
        result.skills.get(1).text == 'Flying'
        result.skills.get(2).text == 'Vigilance'
        result.skills.get(3).text == 'When Archangel Avacyn enters the battlefield, creatures you control gain indestructible until end of turn.'
        result.skills.get(4).text == 'When a non-Angel creature you control dies, transform Archangel Avacyn at the beginning of the next upkeep.'
        result.printRef == 'soi'
        result.printFullName == 'Shadows over Innistrad'
        result.printOrder == 5
        result.edhrecRank == 3460
        result.text == '"Wings that once bore hope are now stained with blood. She is our guardian no longer.\" —Grete, cathar apostate'

        def rule = result.rulings.get(0)
        rule.source == 'wotc'
        rule.published[Calendar.YEAR] == 2016
        rule.text == 'For more information on double-faced cards, see the Shadows over Innistrad mechanics article (http://magic.wizards.com/en/articles/archive/feature/shadows-over-innistrad-mechanics).'

        def type = result.type
        type.basicType == BasicType.TransformableCreature
        type.primaryTypes.size() == 1
        'Legendary' in type.primaryTypes
        type.secondaryTypes.size() == 1
        'Angel' in type.secondaryTypes
        type.colors.size() == 1
        ManaType.WHITE in type.colors

        ManaCost manaCost = result.manaCost
        manaCost.generic.value == 3
        manaCost.white.value == 2
        manaCost.green.value == 0
        manaCost.blue.value == 0
        manaCost.red.value == 0
        manaCost.black.value == 0
        manaCost.colorless.value == 0

        result.attack == 4
        result.defense == 4

        result.transformedName == "Avacyn, the Purifier"
        result.transformedSkills.size() == 2
        result.transformedSkills.get(0).text == 'Flying'
        result.transformedSkills.get(1).text == 'When this creature transforms into Avacyn, the Purifier, it deals 3 damage to each other creature and each opponent.'
        result.transformedAttack == 6
        result.transformedDefense == 5

        def transformedType = result.transformedType
        transformedType.basicType == BasicType.TransformableCreature
        transformedType.primaryTypes.size() == 1
        'Legendary' in transformedType.primaryTypes
        transformedType.secondaryTypes.size() == 1
        'Angel' in transformedType.secondaryTypes
        transformedType.colors.size() == 1
        ManaType.RED in transformedType.colors
    }

    def "convert sorcery card"() {
        given:
        URL url = new URL('https://api.scryfall.com/cards/gtc/2?format=json&pretty=true')

        when:
        bus.message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})
        SorceryCard result = card.get() as SorceryCard

        then:
        result.name == 'Angelic Edict'
        result.rarity == Rarity.COMMON
        result.front.toString() == 'https://img.scryfall.com/cards/png/en/gtc/2.png?1517813031'
        result.art.toString() == 'https://img.scryfall.com/cards/art_crop/en/gtc/2.jpg?1517813031'
        result.source.toString() == 'https://api.scryfall.com/cards/gtc/2'
        result.skills.size() == 1
        result.skills.get(0).text == 'Exile target creature or enchantment.'
        result.printRef == 'gtc'
        result.printFullName == 'Gatecrash'
        result.printOrder == 2
        result.edhrecRank == 7737
        result.text == 'The Boros built a prison in the sky where Azorius statutes couldn\'t restrict their sense of justice.'
        ManaCost manaCost = result.manaCost
        manaCost.generic.value == 4
        manaCost.white.value == 1
        manaCost.green.value == 0
        manaCost.blue.value == 0
        manaCost.red.value == 0
        manaCost.black.value == 0
        manaCost.colorless.value == 0

        def type = result.type
        type.basicType == BasicType.Sorcery
        type.primaryTypes.size() == 0
        type.secondaryTypes.size() == 0
        type.colors.size() == 1
        ManaType.WHITE in type.colors
    }

    def "convert enchantment aura card"() {
        given:
        URL url = new URL('https://api.scryfall.com/cards/m12/3?format=json&pretty=true')

        when:
        bus.message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})
        EnchantmentCard result = card.get() as EnchantmentCard

        then:
        result.name == 'Angelic Destiny'
        result.rarity == Rarity.MYTHIC_RARE
        result.front.toString() == 'https://img.scryfall.com/cards/png/en/m12/3.png?1517813031'
        result.art.toString() == 'https://img.scryfall.com/cards/art_crop/en/m12/3.jpg?1517813031'
        result.source.toString() == 'https://api.scryfall.com/cards/m12/3'
        result.skills.size() == 3
        result.skills.get(0).text == 'Enchant creature'
        result.skills.get(1).text == 'Enchanted creature gets +4/+4, has flying and first strike, and is an Angel in addition to its other types.'
        result.skills.get(2).text == 'When enchanted creature dies, return Angelic Destiny to its owner\'s hand.'
        result.printRef == 'm12'
        result.printFullName == 'Magic 2012'
        result.printOrder == 3
        result.edhrecRank == 2238
        result.text == null
        ManaCost manaCost = result.manaCost
        manaCost.generic.value == 2
        manaCost.white.value == 2
        manaCost.green.value == 0
        manaCost.blue.value == 0
        manaCost.red.value == 0
        manaCost.black.value == 0
        manaCost.colorless.value == 0

        def type = result.type
        type.basicType == BasicType.Enchantment
        type.primaryTypes.size() == 0
        type.secondaryTypes.size() == 1
        'Aura' in type.secondaryTypes
        type.colors.size() == 1
        ManaType.WHITE in type.colors
    }

    def "convert artifact equipment card"() {
        given:
        URL url = new URL('https://api.scryfall.com/cards/avr/212?format=json&pretty=true')

        when:
        bus.message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})
        ArtifactCard result = card.get() as ArtifactCard

        then:
        result.name == 'Angelic Armaments'
        result.rarity == Rarity.UNCOMMON
        result.front.toString() == 'https://img.scryfall.com/cards/png/en/avr/212.png?1517813031'
        result.art.toString() == 'https://img.scryfall.com/cards/art_crop/en/avr/212.jpg?1517813031'
        result.source.toString() == 'https://api.scryfall.com/cards/avr/212'
        result.skills.size() == 2
        result.skills.get(0).text == 'Equipped creature gets +2/+2, has flying, and is a white Angel in addition to its other colors and types.'
        result.skills.get(1).text == 'Equip {4}'
        result.printRef == 'avr'
        result.printFullName == 'Avacyn Restored'
        result.printOrder == 212
        result.edhrecRank == 8034
        result.text == 'Forged in dark hours from the flame that would not die.'
        ManaCost manaCost = result.manaCost
        manaCost.generic.value == 3
        manaCost.white.value == 0
        manaCost.green.value == 0
        manaCost.blue.value == 0
        manaCost.red.value == 0
        manaCost.black.value == 0
        manaCost.colorless.value == 0

        def type = result.type
        type.basicType == BasicType.Artifact
        type.primaryTypes.size() == 0
        type.secondaryTypes.size() == 1
        'Equipment' in type.secondaryTypes
        type.colors.size() == 0
    }

    def "convert planeswalker card"() {
        given:
        URL url = new URL('https://api.scryfall.com/cards/m19/3?format=json&pretty=true')

        when:
        bus.message(CARD_FROM_URL).withContent(url).send()
        await().atMost(2, SECONDS).until({card.hasValue()})
        PlaneswalkerCard result = card.get() as PlaneswalkerCard

        then:
        result.name == 'Ajani, Adversary of Tyrants'
        result.rarity == Rarity.MYTHIC_RARE
        result.front.toString() == 'https://img.scryfall.com/cards/png/en/m19/3.png?1529160847'
        result.art.toString() == 'https://img.scryfall.com/cards/art_crop/en/m19/3.jpg?1529160847'
        result.source.toString() == 'https://api.scryfall.com/cards/m19/3'
        result.skills.size() == 3
        result.skills.get(0).text == '+1: Put a +1/+1 counter on each of up to two target creatures.'
        result.skills.get(1).text == '−2: Return target creature card with converted mana cost 2 or less from your graveyard to the battlefield.'
        result.skills.get(2).text == '−7: You get an emblem with "At the beginning of your end step, create three 1/1 white Cat creature tokens with lifelink."'
        result.printRef == 'm19'
        result.printFullName == 'Core Set 2019'
        result.printOrder == 3
        result.edhrecRank == 0
        result.text == null
        ManaCost manaCost = result.manaCost
        manaCost.generic.value == 2
        manaCost.white.value == 2
        manaCost.green.value == 0
        manaCost.blue.value == 0
        manaCost.red.value == 0
        manaCost.black.value == 0
        manaCost.colorless.value == 0

        def type = result.type
        type.basicType == BasicType.Planeswalker
        type.primaryTypes.size() == 1
        'Legendary' in type.primaryTypes
        type.secondaryTypes.size() == 1
        'Ajani' in type.secondaryTypes
        type.colors.size() == 1
        ManaType.WHITE in type.colors

        result.loyalty == 4
    }
}
