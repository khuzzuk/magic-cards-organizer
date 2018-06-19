package pl.khuzzuk.mtg.organizer.model.card;

import lombok.Getter;
import lombok.Setter;
import pl.khuzzuk.mtg.organizer.model.ManaCost;

@Getter
@Setter
public class SpellCard extends RegularCard {
    private ManaCost manaCost;
}
