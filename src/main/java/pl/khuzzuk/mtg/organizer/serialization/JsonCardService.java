package pl.khuzzuk.mtg.organizer.serialization;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.model.CardQuery;
import pl.khuzzuk.mtg.organizer.model.card.Card;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static pl.khuzzuk.mtg.organizer.events.Event.CARD_FIND;
import static pl.khuzzuk.mtg.organizer.events.Event.CARD_SETS;

@RequiredArgsConstructor
@Component
public class JsonCardService {
    private final Bus<Event> bus;
    private CardsContainer cardsContainer;

    @Autowired
    private void onRepoSerializer(JsonRepoSerializer serializer) {
        cardsContainer = serializer.getCardsContainer();

        bus.subscribingFor(CARD_SETS).withResponse(this::getPrintNames).subscribe();
        bus.subscribingFor(CARD_FIND).mapResponse(this::searchCards).subscribe();
    }

    private SortedSet<String> getPrintNames() {
        return cardsContainer.getCards().stream()
                .map(Card::getPrintFullName)
                .sorted().collect(Collectors.toCollection(TreeSet::new));
    }

    private Set<Card> searchCards(CardQuery cardQuery) {
        return cardsContainer.getCards().stream().filter(cardQuery.getCheck()).collect(Collectors.toSet());
    }
}
