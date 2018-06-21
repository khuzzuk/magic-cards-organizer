package pl.khuzzuk.mtg.organizer.extractor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import pl.khuzzuk.mtg.organizer.model.skill.Skill;
import pl.khuzzuk.mtg.organizer.serialization.PredefinedSkillRepo;

@AllArgsConstructor
public class SkillExtractor {
   PredefinedSkillRepo predefinedSkillRepo;

   List<Skill> extractSkillsFrom(Element profile, int pos) {
      return profile.getElementsByClass("card-text-oracle").get(pos).children().stream()
            .map(Element::text)
            .map(this::retrieveSkillFrom)
            .flatMap(List::stream)
            .collect(Collectors.toList());
   }

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
