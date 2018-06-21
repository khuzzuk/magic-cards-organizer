package pl.khuzzuk.mtg.organizer.extractor.html;

import java.net.MalformedURLException;
import java.util.Map;

import org.jsoup.nodes.Element;
import pl.khuzzuk.mtg.organizer.model.ManaType;
import pl.khuzzuk.mtg.organizer.model.card.BasicLandCard;

class BasicLandStrategy extends SimpleCardStrategy<BasicLandCard> {
   private static final Map<String, ManaType> basicLandManaTypeMapping = Map.of(
         "Plains", ManaType.WHITE,
         "Forest", ManaType.GREEN,
         "Island", ManaType.BLUE,
         "Mountain", ManaType.RED,
         "Swamp", ManaType.BLACK
   );

   @Override
   public void extractFieldsInto(BasicLandCard card, Element profile) throws MalformedURLException {
      super.extractFieldsInto(card, profile);
      card.getType().getColors().add(basicLandManaTypeMapping.get(card.getName()));
   }
}
