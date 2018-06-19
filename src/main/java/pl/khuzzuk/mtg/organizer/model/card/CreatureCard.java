package pl.khuzzuk.mtg.organizer.model.card;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatureCard extends SpellCard {
    private int attack;
    private int defense;
}
