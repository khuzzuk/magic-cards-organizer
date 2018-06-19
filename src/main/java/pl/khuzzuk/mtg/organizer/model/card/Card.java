package pl.khuzzuk.mtg.organizer.model.card;

import java.net.URL;

import lombok.Getter;
import lombok.Setter;
import pl.khuzzuk.mtg.organizer.model.Rarity;
import pl.khuzzuk.mtg.organizer.model.type.Type;

@Getter
@Setter
public abstract class Card {
    private String name;
    private Rarity rarity;
    private URL front;
    private Type type;
    private String printRef;
    private int printOrder;
}
