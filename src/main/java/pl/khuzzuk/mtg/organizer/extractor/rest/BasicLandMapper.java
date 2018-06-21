package pl.khuzzuk.mtg.organizer.extractor.rest;

import lombok.Setter;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import pl.khuzzuk.mtg.organizer.extractor.SkillExtractor;
import pl.khuzzuk.mtg.organizer.model.card.BasicLandCard;

@Mapper(config = CardMapperConfig.class)
public abstract class BasicLandMapper implements CardMapperConfig {
   @Setter
   private SkillExtractor skillExtractor;

   @Override
   @InheritConfiguration
   public abstract BasicLandCard toCard(CardDTO cardDTO);
}
