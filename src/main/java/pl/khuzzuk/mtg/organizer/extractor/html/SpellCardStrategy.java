package pl.khuzzuk.mtg.organizer.extractor.html;

import java.net.MalformedURLException;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.khuzzuk.mtg.organizer.extractor.SkillExtractor;
import pl.khuzzuk.mtg.organizer.extractor.TextToMana;
import pl.khuzzuk.mtg.organizer.model.card.SpellCard;

class SpellCardStrategy<T extends SpellCard> extends RegularCardStrategy<T> {
   public SpellCardStrategy(SkillExtractor skillExtractor) {
      super(skillExtractor);
   }

   @Override
   public void extractFieldsInto(T card, Element profile) throws MalformedURLException {
      super.extractFieldsInto(card, profile);
      Elements manaCostElements = profile.getElementsByClass("card-text-title").get(0).children();
      if (!manaCostElements.isEmpty()) {
         card.setManaCost(TextToMana.from(manaCostElements.text()));
      }
   }
}
