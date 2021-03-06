package pl.khuzzuk.mtg.organizer.extractor.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.annotation.DirtiesContext
import pl.khuzzuk.mtg.organizer.BusTest
import pl.khuzzuk.mtg.organizer.PropertyContainer
import pl.khuzzuk.mtg.organizer.events.Event
import pl.khuzzuk.mtg.organizer.events.EventsConfig
import pl.khuzzuk.mtg.organizer.extractor.rest.data.CardDTO
import pl.khuzzuk.mtg.organizer.extractor.rest.data.CardFaceDTO
import pl.khuzzuk.mtg.organizer.serialization.PredefinedSkillRepo
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest(classes = [EventsConfig.class, ScryfallClientSpec.class])
@ComponentScan(["pl.khuzzuk.mtg.organizer.extractor", "pl.khuzzuk.mtg.organizer.serialization.PredefinedSkillRepo"])
@DirtiesContext
class ScryfallClientSpec extends Specification implements BusTest {
    private static final int connectionTimeout = 10
    @Autowired
    ScryfallClient scryfallClient

    @Shared
    PropertyContainer<CardDTO> card = new PropertyContainer<>()

    void setupSpec() {
        busGetter(Event.CARD_DTO_JSON, card)
    }

    void setup() {
        card.clear()
    }

    def 'download plains basic land'() {
        given:
        URL uri = new URL('https://api.scryfall.com/cards/bbd/250?format=json&pretty=true')

        when:
        bus.message(Event.CARD_FROM_URL).withContent(uri).send()
        checkProperty(card, connectionTimeout)
        CardDTO result = card.get()

        then:
        result.name == 'Plains'
        result.uri.toString().startsWith('https://api.scryfall.com/cards/')
        result.hiResImage
        result.typeLine == 'Basic Land — Plains'
        result.oracleText == '({T}: Add {W}.)'
        def imageUris = result.imageUris
        imageUris.large.toString().contains('https://img.scryfall.com/cards/large/en/bbd/250.jpg?')
        imageUris.png.toString().contains('https://img.scryfall.com/cards/png/en/bbd/250.png?')
        imageUris.artCrop.toString() == 'https://img.scryfall.com/cards/art_crop/en/bbd/250.jpg?1529063642'
        result.colors.length == 0
        result.colorIdentity.length == 1
        result.colorIdentity[0] == "W"
        result.set == 'bbd'
        result.setName == 'Battlebond'
        result.setUri.toString() == 'https://api.scryfall.com/sets/bbd'
        result.rulingsUri.toString().startsWith('https://api.scryfall.com/cards/')
        result.collectorNumber == 250
        !result.digital
        result.rarity == 'common'
        result.flavorText == null
    }

    def 'download land'() {
        given:
        URL uri = new URL('https://api.scryfall.com/cards/md1/17?format=json&pretty=true')

        when:
        bus.message(Event.CARD_FROM_URL).withContent(uri).send()
        checkProperty(card, connectionTimeout)
        CardDTO result = card.get()

        then:
        result.name == 'Vault of the Archangel'
        result.uri.toString().startsWith('https://api.scryfall.com/cards/')
        result.hiResImage
        result.typeLine == 'Land'
        result.oracleText == '{T}: Add {C}.\n{2}{W}{B}, {T}: Creatures you control gain deathtouch and lifelink until end of turn.'
        def imageUris = result.imageUris
        imageUris.large.toString().contains('https://img.scryfall.com/cards/large/en/md1/17.jpg?')
        imageUris.png.toString().contains('https://img.scryfall.com/cards/png/en/md1/17.png?')
        imageUris.artCrop.toString() == 'https://img.scryfall.com/cards/art_crop/en/md1/17.jpg?1517813031'
        result.colors.length == 0
        result.colorIdentity.length == 2
        result.colorIdentity[0] == "B"
        result.colorIdentity[1] == "W"
        result.set == 'md1'
        result.setName == 'Modern Event Deck 2014'
        result.setUri.toString() == 'https://api.scryfall.com/sets/md1'
        result.rulingsUri.toString().startsWith('https://api.scryfall.com/cards/')
        result.collectorNumber == 17
        !result.digital
        result.rarity == 'rare'
        result.flavorText == '"For centuries my creation kept this world in balance. Now only her shadow remains." —Sorin Markov'
    }

    def 'download transformable creature card'() {
        given:
        URL url = new URL('https://api.scryfall.com/cards/soi/5?format=json&pretty=true')

        when:
        bus.message(Event.CARD_FROM_URL).withContent(url).send()
        checkProperty(card, connectionTimeout)
        CardDTO result = card.get()

        then:
        result.name == 'Archangel Avacyn // Avacyn, the Purifier'
        result.uri.toString().startsWith('https://api.scryfall.com/cards/')
        result.hiResImage
        result.typeLine == 'Legendary Creature — Angel // Legendary Creature — Angel'
        result.oracleText == null
        result.imageUris == null
        result.colors == null
        result.colorIdentity.length == 2
        result.colorIdentity[0] == "R"
        result.colorIdentity[1] == "W"
        result.set == 'soi'
        result.setName == 'Shadows over Innistrad'
        result.setUri.toString() == 'https://api.scryfall.com/sets/soi'
        result.rulingsUri.toString().startsWith('https://api.scryfall.com/cards/')
        result.collectorNumber == 5
        !result.digital
        result.rarity == 'mythic'
        result.flavorText == null

        def rulings = result.rulings.data
        rulings.size() == 3
        rulings.get(0).comment == 'For more information on double-faced cards, see the Shadows over Innistrad mechanics article (http://magic.wizards.com/en/articles/archive/feature/shadows-over-innistrad-mechanics).'
        rulings.get(0).source == 'wotc'
        rulings.get(0).publishedAt[Calendar.YEAR] == 2016
        rulings.get(0).publishedAt[Calendar.MONTH] == 6
        rulings.get(0).publishedAt[Calendar.DAY_OF_MONTH] == 13
        rulings.get(1).comment == 'Archangel Avacyn’s delayed triggered ability triggers at the beginning of the next upkeep regardless of whose turn it is.'
        rulings.get(1).source == 'wotc'
        rulings.get(1).publishedAt[Calendar.YEAR] == 2016
        rulings.get(1).publishedAt[Calendar.MONTH] == 3
        rulings.get(1).publishedAt[Calendar.DAY_OF_MONTH] == 8
        rulings.get(2).comment == 'Archangel Avacyn’s delayed triggered ability won’t cause it to transform back into Archangel Avacyn if it has already transformed into Avacyn, the Purifier, perhaps because several creatures died in one turn.'
        rulings.get(2).source == 'wotc'
        rulings.get(2).publishedAt[Calendar.YEAR] == 2016
        rulings.get(2).publishedAt[Calendar.MONTH] == 3
        rulings.get(2).publishedAt[Calendar.DAY_OF_MONTH] == 8

        result.cardFaces.size() == 2
        CardFaceDTO face1 = result.cardFaces.get(0)
        face1.name == 'Archangel Avacyn'
        face1.typeLine == 'Legendary Creature — Angel'
        face1.manaCost == '{3}{W}{W}'
        face1.oracleText == 'Flash\nFlying, vigilance\nWhen Archangel Avacyn enters the battlefield, creatures you control gain indestructible until end of turn.\nWhen a non-Angel creature you control dies, transform Archangel Avacyn at the beginning of the next upkeep.'
        face1.colors.length == 1
        face1.colors[0] == 'W'
        face1.power == '4'
        face1.toughness == '4'
        face1.imageUris.png.toString() == 'https://img.scryfall.com/cards/png/en/soi/5a.png?1518204266'
        face1.imageUris.artCrop.toString() == 'https://img.scryfall.com/cards/art_crop/en/soi/5a.jpg?1518204266'

        CardFaceDTO face2 = result.cardFaces.get(1)
        face2.name == 'Avacyn, the Purifier'
        face2.typeLine == 'Legendary Creature — Angel'
        face2.manaCost == ''
        face2.oracleText == 'Flying\nWhen this creature transforms into Avacyn, the Purifier, it deals 3 damage to each other creature and each opponent.'
        face2.colors.length == 1
        face2.colors[0] == 'R'
        face2.power == '6'
        face2.toughness == '5'
        face2.imageUris.png.toString() == 'https://img.scryfall.com/cards/png/en/soi/5b.png?1518204266'
        face2.imageUris.artCrop.toString() == 'https://img.scryfall.com/cards/art_crop/en/soi/5b.jpg?1518204266'
    }

    @Bean
    PredefinedSkillRepo predefinedSkillRepo() {
        return Mock(PredefinedSkillRepo)
    }
}
