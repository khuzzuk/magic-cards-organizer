package pl.khuzzuk.mtg.organizer.serialization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.initialize.Identification;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;
import pl.khuzzuk.mtg.organizer.model.skill.PredefinedSkill;

@RequiredArgsConstructor
@Identification(Event.PREDEFINED_SKILLS)
public class PredefinedSkillRepo implements Loadable {
   private final Bus<Event> bus;
   private PredefinedSkillContainer predefinedSkillContainer;

   @Override
   public void load() {
      try {
         String json = Files.readAllLines(Paths.get("predefinedSkills.json")).stream().collect(Collectors.joining());
         ObjectMapper mapper = new ObjectMapper();
         predefinedSkillContainer = mapper.readValue(json, PredefinedSkillContainer.class);
      } catch (IOException e) {
         bus.message(Event.ERROR).withContent("Predefined skills read error").send();
      }
   }

   public List<PredefinedSkill> findAll() {
      return predefinedSkillContainer.predefinedSkills;
   }

   @Getter
   @Setter
   static class PredefinedSkillContainer {
      private List<PredefinedSkill> predefinedSkills;
   }
}
