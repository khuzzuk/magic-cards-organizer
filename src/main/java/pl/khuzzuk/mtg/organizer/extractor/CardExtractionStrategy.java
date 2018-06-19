package pl.khuzzuk.mtg.organizer.extractor;

import org.jsoup.nodes.Element;
import pl.khuzzuk.mtg.organizer.model.card.Card;

public interface CardExtractionStrategy<T extends Card> {
   void extractFieldsInto(T card, Element profile);
}
