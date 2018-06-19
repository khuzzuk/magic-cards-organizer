package pl.khuzzuk.mtg.organizer.model.card;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pl.khuzzuk.mtg.organizer.model.skill.Skill;

@Getter
@Setter
public abstract class RegularCard extends Card {
   private String text;
   private List<Skill> skills;
}
