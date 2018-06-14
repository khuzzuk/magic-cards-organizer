package pl.khuzzuk.mtg.organizer.serialization

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.initialize.Loadable
import pl.khuzzuk.mtg.organizer.model.Card

class JsonSerializer implements Loadable {
    private Bus<Event> bus
    private ObjectMapper objectMapper

    JsonSerializer(Bus<Event> bus) {
        this.bus = bus
    }

    @Override
    void load() {
        objectMapper = new ObjectMapper()
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
        objectMapper.enableDefaultTyping()

        bus.subscribingFor(Event.CARD_DATA).accept({serializeCard(it as Card)}).subscribe()
    }

    void serializeCard(Card card) {
        objectMapper.writeValueAsString(card)
    }
}
