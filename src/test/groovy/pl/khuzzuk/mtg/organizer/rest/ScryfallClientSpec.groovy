package pl.khuzzuk.mtg.organizer.rest

import pl.khuzzuk.mtg.organizer.BusTest
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.PropertyContainer
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.TimeUnit

import static org.awaitility.Awaitility.await

class ScryfallClientSpec extends Specification implements BusTest {
    @Shared
    PropertyContainer<CardDTO> card = new PropertyContainer<>()

    void setupSpec() {
        setupBus()
        new ScryfallClient(bus).load()
        subscribingFor(Event.CARD_DTO_JSON).accept({ card.put(it as CardDTO) }).subscribe()
    }

    void setup() {
        card.clear()
    }

    void closeSpec() {
        closeBus()
    }

    def 'download land cardDTO'() {
        given:
        URL uri = new URL('https://api.scryfall.com/cards/md1/17?format=json&pretty=true')

        when:
        bus.message(Event.CARD_FROM_URL).withContent(uri).send()
        await().atMost(3, TimeUnit.SECONDS).until({card.hasValue()})
        CardDTO result = card.get()

        then:
        result.name == 'Vault of the Archangel'
        result.uri.toString() == 'https://api.scryfall.com/cards/md1/17'
        result.hiResImage
        result.typeLine == 'Land'
        result.oracleText == '{T}: Add {C}.\n{2}{W}{B}, {T}: Creatures you control gain deathtouch and lifelink until end of turn.'
        def imageUris = result.imageUris
        imageUris.large.toString() == 'https://img.scryfall.com/cards/large/en/md1/17.jpg?1517813031'
        imageUris.png.toString() == 'https://img.scryfall.com/cards/png/en/md1/17.png?1517813031'
        imageUris.artCrop.toString() == 'https://img.scryfall.com/cards/art_crop/en/md1/17.jpg?1517813031'
        result.colors.length == 0
        result.colorIdentity.length == 2
        result.colorIdentity == ["B", "W"] as String[]
    }
}
