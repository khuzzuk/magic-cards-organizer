package pl.khuzzuk.mtg.organizer.extractor;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pl.khuzzuk.mtg.organizer.model.skill.Skill;
import pl.khuzzuk.mtg.organizer.serialization.PredefinedSkillRepo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class SkillExtractor {
   PredefinedSkillRepo predefinedSkillRepo;

   public List<Skill> extractSkillsFrom(List<String> lines) {
      return lines.stream()
            .map(this::retrieveSkillFrom)
            .flatMap(List::stream)
            .collect(Collectors.toList());
   }

   private List<? extends Skill> retrieveSkillFrom(String line) {
      return containsCustomSkill(line)
            ? List.of(new Skill(line))
            : predefinedSkillRepo.findAllBy(splitPotentialSkillElements(line));

   }

   private boolean containsCustomSkill(String line) {
      return splitPotentialSkillElements(line).stream()
            .anyMatch(s -> !predefinedSkillRepo.exists(s));
   }

   private List<String> splitPotentialSkillElements(String line) {
      return Arrays.stream(StringUtils.split(line))
            .map(s -> s.replace(",", ""))
            .collect(Collectors.toList());
   }
}
