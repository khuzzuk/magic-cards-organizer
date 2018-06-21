package pl.khuzzuk.mtg.organizer.model.card;

import lombok.Getter;
import lombok.Setter;
import pl.khuzzuk.mtg.organizer.model.Rarity;
import pl.khuzzuk.mtg.organizer.model.type.Type;

import java.net.URL;

@Getter
@Setter
public abstract class Card {
    private String name;
    private Rarity rarity;
    private URL front;
    private URL art;
    private Type type;
    private String printRef;
    private String printFullName;
    private int printOrder;
    private URL source;
    private int edhrecRank;
}
