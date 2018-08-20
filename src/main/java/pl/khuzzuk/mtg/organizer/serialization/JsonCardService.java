package pl.khuzzuk.mtg.organizer.serialization;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.model.CardQuery;
import pl.khuzzuk.mtg.organizer.model.card.Card;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.khuzzuk.mtg.organizer.events.Event.CARD_FIND;
import static pl.khuzzuk.mtg.organizer.events.Event.CARD_SETS;

@RequiredArgsConstructor
@Component
public class JsonCardService implements InitializingBean {
    private final Bus<Event> bus;
    private final JsonRepoSerializer jsonRepoSerializer;

    public void afterPropertiesSet() {
        bus.subscribingFor(CARD_SETS).withResponse(this::getPrintNames).subscribe();
        bus.subscribingFor(CARD_FIND).mapResponse(this::searchCards).subscribe();
    }

    private Stream<Card> cards() {
        return jsonRepoSerializer.getCardsContainer().getCards().stream();
    }

    private SortedSet<String> getPrintNames() {
        return cards().map(Card::getPrintFullName).sorted().collect(Collectors.toCollection(TreeSet::new));
    }

    private Set<Card> searchCards(CardQuery cardQuery) {
        return cards().filter(cardQuery.getCheck()).collect(Collectors.toSet());
    }
}
