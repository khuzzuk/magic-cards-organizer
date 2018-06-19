package pl.khuzzuk.mtg.organizer.extractor;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.khuzzuk.mtg.organizer.model.Rarity;
import pl.khuzzuk.mtg.organizer.model.card.Card;

class SimpleCardStrategy<T extends Card> implements CardExtractionStrategy<T> {
   @Override
   public void extractFieldsInto(T card, Element profile) throws MalformedURLException {
      card.setName(getName(profile, 0));
      card.setFront(getImageUrl(profile, "card-image-front"));

      Element printsCurrent = profile.getElementsByClass("prints-current").get(0);
      card.setPrintRef(printsCurrent.getElementsByClass("prints-current-set-name").get(0).ownText());
      String printDetails = printsCurrent.getElementsByClass("prints-current-set-details").get(0).ownText();
      card.setPrintOrder(Integer.valueOf(printDetails.substring(1, printDetails.indexOf(' '))));
      card.setRarity(mapFrom(printDetails));
   }

   String getName(Element profile, int pos) {
      Element titleText = profile.getElementsByClass("card-text-title").get(pos);
      Elements manaCostElements = titleText.children();
      return titleText.text().replace(manaCostElements.text(), "").trim();
   }

   URL getImageUrl(Element profile, String locator) throws MalformedURLException {
      String imageUrl = profile.getElementsByClass(locator).get(0)
            .getElementsByAttribute("src").get(0).attributes().get("src");
      return new URL(imageUrl);
   }

   private Rarity mapFrom(String htmlValue) {
      String lowerCaseValue = htmlValue.toLowerCase();
      if (lowerCaseValue.contains("mythic rare")) return Rarity.MYTHIC_RARE;
      if (lowerCaseValue.contains("rare")) return Rarity.RARE;
      if (lowerCaseValue.contains("uncommon")) return Rarity.UNCOMMON;
      if (lowerCaseValue.contains("common")) return Rarity.COMMON;
      throw new IllegalArgumentException(htmlValue);
   }
}
