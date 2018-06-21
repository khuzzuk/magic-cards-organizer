package pl.khuzzuk.mtg.organizer.extractor.rest;

import pl.khuzzuk.mtg.organizer.extractor.SkillExtractor;
import pl.khuzzuk.mtg.organizer.extractor.TextToMana;
import pl.khuzzuk.mtg.organizer.model.ManaCost;
import pl.khuzzuk.mtg.organizer.model.card.SpellCard;
import pl.khuzzuk.mtg.organizer.model.type.Type;

public abstract class SpellCardMapper<T extends SpellCard> extends RegularCardMapper<T> {
    public SpellCardMapper(SkillExtractor skillExtractor) {
        super(skillExtractor);
    }

    @Override
    public T toCard(CardDTO cardDTO, Type type) {
        T card = super.toCard(cardDTO, type);
        card.setManaCost(manaCostFrom(cardDTO.getManaCost()));
        return card;
    }

    ManaCost manaCostFrom(String manaCost) {
        return TextToMana.from(manaCost);
    }
}
