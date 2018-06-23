package pl.khuzzuk.mtg.organizer.serialization;

import lombok.Getter;
import lombok.Setter;
import pl.khuzzuk.mtg.organizer.model.card.Card;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
class CardsContainer {
    private Set<Card> cards = new HashSet<>();
}
