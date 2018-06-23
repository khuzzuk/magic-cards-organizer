package pl.khuzzuk.mtg.organizer.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;
import pl.khuzzuk.mtg.organizer.model.card.Card;

@RequiredArgsConstructor
public class JsonCardSerializer implements Loadable {
    private final Bus<Event> bus;
    private ObjectMapper objectMapper;

    @Override
    public void load() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT);

        bus.subscribingFor(Event.CARD_DATA).accept(this::serializeCard).subscribe();
    }

    private void serializeCard(Card card) {
        try {
            objectMapper.writeValueAsString(card);
        } catch (JsonProcessingException e) {
            bus.message(Event.ERROR).withContent("Cannot serialize card to JSON").send();
        }
    }
}
