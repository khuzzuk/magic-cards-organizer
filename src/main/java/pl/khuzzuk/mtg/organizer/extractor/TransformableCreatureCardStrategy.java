package pl.khuzzuk.mtg.organizer.extractor;

import java.net.MalformedURLException;

import org.jsoup.nodes.Element;
import pl.khuzzuk.mtg.organizer.model.card.TransformableCreatureCard;

class TransformableCreatureCardStrategy extends CreatureCardStrategy<TransformableCreatureCard> {
   private TypeExtractor typeExtractor;

   public TransformableCreatureCardStrategy(SkillExtractor skillExtractor, TypeExtractor typeExtractor) {
      super(skillExtractor);
      this.typeExtractor = typeExtractor;
   }

   @Override
   public void extractFieldsInto(TransformableCreatureCard card, Element profile) throws MalformedURLException {
      super.extractFieldsInto(card, profile);
      card.setTransformedType(typeExtractor.extractTransformedTypeFrom(profile));
      card.setTransformedName(getName(profile, 1));
   }
}
