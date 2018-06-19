package pl.khuzzuk.mtg.organizer.model.card;

import java.net.URL;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pl.khuzzuk.mtg.organizer.model.skill.Skill;
import pl.khuzzuk.mtg.organizer.model.type.Type;

@Getter
@Setter
public class TransformableCreatureCard extends CreatureCard {
   private String transformedName;
   private Type transformedType;
   private List<Skill> transformedSkills;
   private int transformedAttack;
   private int transformedDefense;
   private URL back;
}
