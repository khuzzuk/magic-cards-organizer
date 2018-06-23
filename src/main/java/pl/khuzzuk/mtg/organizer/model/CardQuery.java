package pl.khuzzuk.mtg.organizer.model;

import lombok.Getter;
import pl.khuzzuk.mtg.organizer.model.card.Card;

import java.util.function.Predicate;

public class CardQuery {
    private static final Predicate<Card> BASE = card -> true;

    @Getter
    private Predicate<Card> check = BASE;

    public CardQuery withName(String name) {
        CardQuery cardQuery = new CardQuery();
        cardQuery.check = this.check.and(card -> card.getName().equalsIgnoreCase(name));
        return cardQuery;
    }
}
