package pl.khuzzuk.mtg.organizer.extractor.rest;

import pl.khuzzuk.mtg.organizer.extractor.SkillExtractor;
import pl.khuzzuk.mtg.organizer.extractor.rest.data.CardDTO;
import pl.khuzzuk.mtg.organizer.model.card.PlaneswalkerCard;
import pl.khuzzuk.mtg.organizer.model.type.Type;

public class PlaneswalkerCardMapper extends SpellCardMapper<PlaneswalkerCard> {
    public PlaneswalkerCardMapper(SkillExtractor skillExtractor) {
        super(skillExtractor);
    }

    @Override
    public PlaneswalkerCard toCard(CardDTO cardDTO, Type type) {
        PlaneswalkerCard card = super.toCard(cardDTO, type);
        card.setLoyalty(cardDTO.getLoyalty());
        return card;
    }
}
