package pl.khuzzuk.mtg.organizer.extractor;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;
import pl.khuzzuk.mtg.organizer.model.Card;
import pl.khuzzuk.mtg.organizer.model.CreatureCard;
import pl.khuzzuk.mtg.organizer.model.SpellCard;
import pl.khuzzuk.mtg.organizer.model.type.BasicType;
import pl.khuzzuk.mtg.organizer.model.type.Type;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@RequiredArgsConstructor
public class HtmlCardExtractor implements Loadable {
    private final Bus<Event> bus;

    @Override
    public void load() {
        bus.subscribingFor(Event.CARD_FROM_URL).accept(this::cardFrom).subscribe();
    }

    private void cardFrom(String url) {
        Document doc = downloadPage(url);
        if (doc != null) {
            Element profile = doc.getElementById("main").getElementsByClass("card-profile").get(0);
            Type type = determineBasicTypeFrom(profile.getElementsByClass("card-text-type-line").text());

            Card card = createCreatureCard(profile);
            card.setType(type);
            bus.message(Event.CARD_DATA).withContent(card).send();
        }
    }

    private CreatureCard createCreatureCard(Element profile) {
        CreatureCard card = new CreatureCard();
        populateSpellCardFields(card, profile);
        String[] stats = profile.getElementsByClass("card-text-stats").get(0).text().split("/");
        card.setAttack(Integer.parseInt(stats[0]));
        card.setDefense(Integer.parseInt(stats[1]));
        return card;
    }

    private <T extends SpellCard> void populateSpellCardFields(T card, Element profile) {
        populateBasicFields(card, profile);
        Elements manaCostElements = profile.getElementsByClass("card-text-title").get(0).children();
        if (!manaCostElements.isEmpty()) {
            card.setManaCost(TextToMana.from(manaCostElements.text()));
        }
    }

    private <T extends Card> void populateBasicFields(T card, Element profile) {
        Element titleText = profile.getElementsByClass("card-text-title").get(0);
        Elements manaCostElements = titleText.children();
        String cardName = titleText.text().replace(manaCostElements.text(), "").trim();
        card.setName(cardName);
        card.setText(profile.getElementsByClass("card-text-flavor").get(0).child(0).text());
        String imageUrl = profile.getElementsByClass("card-image-front").get(0).getElementsByAttribute("src").get(0).attributes().get("src");
        card.setFront(getImageUrl(imageUrl));
    }

    private static Type determineBasicTypeFrom(String text) {
        Type type = new Type();
        String[] lineElements = text.split(" — ");
        if (lineElements[0].contains(" ")) {
            String[] primaryTypes = lineElements[0].split(" ");
            type.setBasicType(BasicType.valueOf(primaryTypes[1]));
            type.getPrimaryTypes().add(primaryTypes[0]);
        } else {
            type.setBasicType(BasicType.valueOf(lineElements[0]));
        }

        if (lineElements.length > 1) {
            String[] secondaryTypes = lineElements[1].split(" ");
            for (String secondaryType : secondaryTypes) {
                type.getSecondaryTypes().add(secondaryType);
            }

        }

        return type;
    }

    private Document downloadPage(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            bus.message(Event.ERROR).withContent(String.format("Cannot download page from url: %s", url)).send();
            return null;
        }
    }

    private URL getImageUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            bus.message(Event.ERROR).withContent("Cannot find image for card").send();
            return null;
        }
    }
}