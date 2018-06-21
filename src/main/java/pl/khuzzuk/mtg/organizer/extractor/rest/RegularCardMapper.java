package pl.khuzzuk.mtg.organizer.extractor.rest;

import java.util.List;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import pl.khuzzuk.mtg.organizer.extractor.SkillExtractor;
import pl.khuzzuk.mtg.organizer.extractor.TextToMana;
import pl.khuzzuk.mtg.organizer.model.ManaCost;
import pl.khuzzuk.mtg.organizer.model.Rarity;
import pl.khuzzuk.mtg.organizer.model.card.LandCard;
import pl.khuzzuk.mtg.organizer.model.skill.Skill;

@MapperConfig
@Mapper(config = CardMapperConfig.class)
public abstract class RegularCardMapper implements CardMapperConfig {
   @Setter
   private SkillExtractor skillExtractor;

   @Override
   @Mapping(source = "oracleText", target = "skills")
   @InheritConfiguration
   public abstract LandCard toCard(CardDTO cardDTO);

   Rarity rarityFrom(String rarity) {
      return Rarity.valueOf(rarity.toUpperCase());
   }

   ManaCost manaCostFrom(String manaCost) {
      return TextToMana.from(manaCost);
   }

   List<Skill> skillsFrom(String skillLine) {
      List<String> skillsList = List.of(StringUtils.split(skillLine, "\n"));
      return skillExtractor.extractSkillsFrom(skillsList);
   }
}
