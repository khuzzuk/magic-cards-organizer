package pl.khuzzuk.mtg.organizer.serialization;

import lombok.RequiredArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;
import pl.khuzzuk.mtg.organizer.model.CardQuery;
import pl.khuzzuk.mtg.organizer.model.card.Card;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static pl.khuzzuk.mtg.organizer.events.Event.CARD_FIND;
import static pl.khuzzuk.mtg.organizer.events.Event.CARD_SETS;

@RequiredArgsConstructor
public class JsonCardService implements Loadable {
    private final Bus<Event> bus;
    private CardsContainer cardsContainer;

    @Override
    public void load() {
        bus.subscribingFor(Event.JSON_REPO_SERIALIZER).accept(this::onRepoSerializer).subscribe();
    }

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
