package pl.khuzzuk.mtg.organizer.model.card;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Rule {
    private String text;
    private Date published;
    private String source;
}
