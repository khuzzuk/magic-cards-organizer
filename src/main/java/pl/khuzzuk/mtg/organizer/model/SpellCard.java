package pl.khuzzuk.mtg.organizer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpellCard extends Card {
    private ManaCost manaCost;
}
