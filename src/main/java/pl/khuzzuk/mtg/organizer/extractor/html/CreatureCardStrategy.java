package pl.khuzzuk.mtg.organizer.extractor.html;

import java.net.MalformedURLException;

import org.jsoup.nodes.Element;
import pl.khuzzuk.mtg.organizer.extractor.SkillExtractor;
import pl.khuzzuk.mtg.organizer.model.card.CreatureCard;

class CreatureCardStrategy<T extends CreatureCard> extends SpellCardStrategy<T> {
   public CreatureCardStrategy(SkillExtractor skillExtractor) {
      super(skillExtractor);
   }

   @Override
   public void extractFieldsInto(T card, Element profile) throws MalformedURLException {
      super.extractFieldsInto(card, profile);
      int[] stats = extractStats(profile, 0);
      card.setAttack(stats[0]);
      card.setDefense(stats[1]);
   }

   int[] extractStats(Element profile, int pos) {
      String[] stats = profile.getElementsByClass("card-text-stats").get(pos).text().split("/");
      return new int[]{Integer.valueOf(stats[0]), Integer.valueOf(stats[1])};
   }
}
