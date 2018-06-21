package pl.khuzzuk.mtg.organizer.extractor.html;

import org.jsoup.nodes.Element;
import pl.khuzzuk.mtg.organizer.extractor.SkillExtractor;
import pl.khuzzuk.mtg.organizer.extractor.TypeExtractor;
import pl.khuzzuk.mtg.organizer.model.card.TransformableCreatureCard;

import java.net.MalformedURLException;

class TransformableCreatureCardStrategy extends CreatureCardStrategy<TransformableCreatureCard> {

   public TransformableCreatureCardStrategy(SkillExtractor skillExtractor) {
      super(skillExtractor);
   }

   @Override
   public void extractFieldsInto(TransformableCreatureCard card, Element profile) throws MalformedURLException {
      super.extractFieldsInto(card, profile);
      card.setTransformedType(TypeExtractor.extractTransformedTypeFrom(profile));
      card.setTransformedName(getName(profile, 1));
      card.setTransformedSkills(skillExtractor.extractSkillsFrom(profile, 1));
      card.setBack(getImageUrl(profile, "card-image-back"));

      int[] stats = extractStats(profile, 1);
      card.setTransformedAttack(stats[0]);
      card.setTransformedDefense(stats[1]);
   }
}
