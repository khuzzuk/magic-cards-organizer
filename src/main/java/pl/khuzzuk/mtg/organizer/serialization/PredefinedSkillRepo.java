package pl.khuzzuk.mtg.organizer.serialization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
import pl.khuzzuk.mtg.organizer.model.skill.Skill;

@RequiredArgsConstructor
@Identification(Event.PREDEFINED_SKILLS)
public class PredefinedSkillRepo implements Loadable {
   private final Bus<Event> bus;
   private Map<String, PredefinedSkill> predefinedSkills;

   @Override
   public void load() {
      try {
         Path jsonFilePath = Paths.get(getClass().getClassLoader().getResource("predefinedSkills.json").getPath());
         String json = Files.readAllLines(jsonFilePath).stream().collect(Collectors.joining());
         ObjectMapper mapper = new ObjectMapper();
         PredefinedSkillContainer predefinedSkillContainer = mapper.readValue(json, PredefinedSkillContainer.class);
         predefinedSkills = predefinedSkillContainer.predefinedSkills.stream()
               .collect(Collectors.toMap(Skill::getText, Function.identity()));
      } catch (IOException e) {
         bus.message(Event.ERROR).withContent("Predefined skills read error").send();
      }
   }

   public boolean exists(String name) {
      return predefinedSkills.containsKey(name);
   }

   public List<PredefinedSkill> findAllBy(List<String> names) {
      return names.stream()
            .filter(this::exists)
            .map(predefinedSkills::get)
            .collect(Collectors.toList());
   }

   @Getter
   @Setter
   static class PredefinedSkillContainer {
      private List<PredefinedSkill> predefinedSkills;
   }
}
