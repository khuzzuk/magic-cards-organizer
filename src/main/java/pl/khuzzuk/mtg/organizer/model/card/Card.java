package pl.khuzzuk.mtg.organizer.model.card;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.khuzzuk.mtg.organizer.model.Rarity;
import pl.khuzzuk.mtg.organizer.model.type.Type;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = {"name", "printRef", "printOrder"})
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "object")
@ToString
public abstract class Card {
    private String name;
    private Rarity rarity;
    private URL front;
    private URL art;
    private URL downloadedFront;
    private URL downloadedArt;
    private Type type;
    private String printRef;
    private String printFullName;
    private int printOrder;
    private URL source;
    private int edhrecRank;
    private List<Rule> rulings = new ArrayList<>();
}
