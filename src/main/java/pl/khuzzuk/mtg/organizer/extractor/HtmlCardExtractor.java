package pl.khuzzuk.mtg.organizer.extractor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;
import pl.khuzzuk.mtg.organizer.model.card.BasicLandCard;
import pl.khuzzuk.mtg.organizer.model.card.Card;
import pl.khuzzuk.mtg.organizer.model.card.CreatureCard;
import pl.khuzzuk.mtg.organizer.model.card.LandCard;
import pl.khuzzuk.mtg.organizer.model.card.TransformableCreatureCard;
import pl.khuzzuk.mtg.organizer.model.type.BasicType;
import pl.khuzzuk.mtg.organizer.model.type.Type;
import pl.khuzzuk.mtg.organizer.serialization.PredefinedSkillRepo;

@RequiredArgsConstructor
public class HtmlCardExtractor implements Loadable {
    private final Bus<Event> bus;
    private Map<BasicType, Supplier<? extends Card>> cardSuppliers;
    private Map<BasicType, CardExtractionStrategy> extractors;
    private TypeExtractor typeExtractor;

    @Override
    public void load() {
        bus.subscribingFor(Event.PREDEFINED_SKILLS).accept(this::afterPredefinedSkillRepoSet).subscribe();
    }

    private void afterPredefinedSkillRepoSet(PredefinedSkillRepo predefinedSkillRepo) {
        typeExtractor = new TypeExtractor();
        SkillExtractor skillExtractor = new SkillExtractor(predefinedSkillRepo);
        cardSuppliers = Map.of(
              BasicType.TransformableCreature, TransformableCreatureCard::new,
              BasicType.Creature, CreatureCard::new,
              BasicType.Land, LandCard::new,
              BasicType.BasicLand, BasicLandCard::new
        );
        extractors = Map.of(
              BasicType.TransformableCreature, new TransformableCreatureCardStrategy(skillExtractor, typeExtractor),
              BasicType.Creature, new CreatureCardStrategy(skillExtractor)
        );
        bus.subscribingFor(Event.CARD_FROM_URL).accept(this::cardFrom).subscribe();
    }

    @SuppressWarnings("unchecked")
    private void cardFrom(String url) {
        Document doc = downloadPage(url);
        if (doc != null) {
            try {
                Element profile = doc.getElementById("main").getElementsByClass("card-profile").get(0);
                Type type = typeExtractor.extractTypeFrom(profile);
                Card card = cardSuppliers.get(type.getBasicType()).get();
                card.setType(type);
                extractors.get(type.getBasicType()).extractFieldsInto(card, profile);
                bus.message(Event.CARD_DATA).withContent(card).send();
            } catch (MalformedURLException e) {
                bus.message(Event.ERROR).withContent(e.getMessage()).send();
            }
        }
    }

    private Document downloadPage(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            bus.message(Event.ERROR).withContent(String.format("Cannot download page from url: %s", url)).send();
            return null;
        }
    }
}
