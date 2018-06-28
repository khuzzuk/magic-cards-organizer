package pl.khuzzuk.mtg.organizer.model.card;

import lombok.Getter;
import lombok.Setter;
import pl.khuzzuk.mtg.organizer.model.skill.Skill;
import pl.khuzzuk.mtg.organizer.model.type.Type;

import java.net.URL;
import java.util.List;

@Getter
@Setter
public class TransformableCreatureCard extends CreatureCard {
   private String transformedName;
   private Type transformedType;
   private List<Skill> transformedSkills;
   private String transformedAttack;
   private String transformedDefense;
   private URL back;
   private URL backArt;
}
