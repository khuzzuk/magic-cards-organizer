package pl.khuzzuk.mtg.organizer.extractor.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.extractor.SkillExtractor;
import pl.khuzzuk.mtg.organizer.extractor.TypeExtractor;
import pl.khuzzuk.mtg.organizer.extractor.rest.data.CardDTO;
import pl.khuzzuk.mtg.organizer.model.card.Card;
import pl.khuzzuk.mtg.organizer.model.type.BasicType;
import pl.khuzzuk.mtg.organizer.model.type.Type;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class CardJSONConverter implements InitializingBean {
    private final Bus<Event> bus;
    private final SkillExtractor skillExtractor;
    private Map<BasicType, CardMapper> mappers;

    @Override
    public void afterPropertiesSet() {
        mappers = Map.of(
                BasicType.Land, new RegularCardMapper(skillExtractor),
                BasicType.Sorcery, new SpellCardMapper(skillExtractor),
                BasicType.Enchantment, new SpellCardMapper(skillExtractor),
                BasicType.Instant, new SpellCardMapper(skillExtractor),
                BasicType.Artifact, new SpellCardMapper(skillExtractor),
                BasicType.Planeswalker, new PlaneswalkerCardMapper(skillExtractor),
                BasicType.Creature, new CreatureCardMapper(skillExtractor),
                BasicType.TransformableCreature, new TransformableCreatureMapper(skillExtractor)
        );

        bus.subscribingFor(Event.CARD_DTO_JSON).accept(this::toCard).subscribe();
    }

    private void toCard(CardDTO cardDTO) {
        Type type = TypeExtractor.from(cardDTO);
        Card card = mappers.get(type.getBasicType()).toCard(cardDTO, type);
        bus.message(Event.CARD_DATA).withContent(card).send();
    }
}
