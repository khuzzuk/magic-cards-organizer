package pl.khuzzuk.mtg.organizer.extractor.rest;

import pl.khuzzuk.mtg.organizer.extractor.SkillExtractor;
import pl.khuzzuk.mtg.organizer.model.card.CreatureCard;
import pl.khuzzuk.mtg.organizer.model.type.Type;

public class CreatureCardMapper<T extends CreatureCard> extends SpellCardMapper<T> {
    public CreatureCardMapper(SkillExtractor skillExtractor) {
        super(skillExtractor);
    }

    @Override
    public T toCard(CardDTO cardDTO, Type type) {
        T card = super.toCard(cardDTO, type);
        card.setAttack(cardDTO.getPower());
        card.setDefense(cardDTO.getToughness());
        return card;
    }
}
