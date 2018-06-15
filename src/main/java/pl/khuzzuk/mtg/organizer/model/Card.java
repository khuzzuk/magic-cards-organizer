package pl.khuzzuk.mtg.organizer.model;

import lombok.Getter;
import lombok.Setter;
import pl.khuzzuk.mtg.organizer.model.type.Type;

import java.net.URL;

@Getter
@Setter
public class Card {
    private String name;
    private String text;
    private URL front;
    private Type type;
}
