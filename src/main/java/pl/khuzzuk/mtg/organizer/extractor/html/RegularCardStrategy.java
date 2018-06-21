package pl.khuzzuk.mtg.organizer.extractor.html;

import java.net.MalformedURLException;

import lombok.AllArgsConstructor;
import org.jsoup.nodes.Element;
import pl.khuzzuk.mtg.organizer.extractor.SkillExtractor;
import pl.khuzzuk.mtg.organizer.model.card.RegularCard;

@AllArgsConstructor
class RegularCardStrategy<T extends RegularCard> extends SimpleCardStrategy<T> {
   SkillExtractor skillExtractor;

   @Override
   public void extractFieldsInto(T card, Element profile) throws MalformedURLException {
      super.extractFieldsInto(card, profile);
      card.setText(profile.getElementsByClass("card-text-flavor").get(0).child(0).text());
      card.setSkills(skillExtractor.extractSkillsFrom(profile, 0));
   }
}
