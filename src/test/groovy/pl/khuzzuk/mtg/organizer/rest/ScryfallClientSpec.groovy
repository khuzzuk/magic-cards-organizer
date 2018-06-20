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
        await().atMost(2, TimeUnit.SECONDS).until({card.hasValue()})
        CardDTO result = card.get()

        then:
        result.name == 'Vault of the Archangel'
    }
}
