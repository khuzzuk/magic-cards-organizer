package pl.khuzzuk.mtg.organizer.web;

import org.springframework.stereotype.Component;
import pl.khuzzuk.mtg.organizer.model.card.Card;
import pl.khuzzuk.mtg.organizer.model.type.BasicType;

import java.util.Map;

import static pl.khuzzuk.mtg.organizer.model.type.BasicType.*;

@Component
public class CardTypeNameViewService {
    private Map<BasicType, String> names = Map.of(
            Artifact, "Artifact",
            BasicLand, "Basic Land",
            Creature, "Creature",
            TransformableCreature, "Creature",
            Enchantment, "Enchantment",
            Instant, "Instant",
            Land, "Land",
            Planeswalker, "Planeswalker",
            Sorcery, "Sorcery"
    );

    public String getNameByType(Card card) {
        return names.get(card.getType().getBasicType());
    }
}
