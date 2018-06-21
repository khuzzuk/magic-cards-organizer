package pl.khuzzuk.mtg.organizer.extractor.rest;

import lombok.RequiredArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.extractor.SkillExtractor;
import pl.khuzzuk.mtg.organizer.extractor.TypeExtractor;
import pl.khuzzuk.mtg.organizer.extractor.rest.CardDTO;
import pl.khuzzuk.mtg.organizer.extractor.rest.CardMapper;
import pl.khuzzuk.mtg.organizer.extractor.rest.CreatureCardMapper;
import pl.khuzzuk.mtg.organizer.extractor.rest.RegularCardMapper;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;
import pl.khuzzuk.mtg.organizer.model.card.Card;
import pl.khuzzuk.mtg.organizer.model.type.BasicType;
import pl.khuzzuk.mtg.organizer.model.type.Type;
import pl.khuzzuk.mtg.organizer.serialization.PredefinedSkillRepo;

import java.util.Map;

@RequiredArgsConstructor
public class CardJSONConverter implements Loadable {
    private final Bus<Event> bus;
    private Map<BasicType, CardMapper> mappers;

    @Override
    public void load() {
        bus.subscribingFor(Event.PREDEFINED_SKILLS).accept(this::setPredefinedSkillRepo).subscribe();
    }

    private void setPredefinedSkillRepo(PredefinedSkillRepo predefinedSkillRepo) {
        SkillExtractor skillExtractor = new SkillExtractor(predefinedSkillRepo);
        mappers = Map.of(
                BasicType.Land, new RegularCardMapper(skillExtractor),
                BasicType.Creature, new CreatureCardMapper(skillExtractor)
        );
        bus.subscribingFor(Event.CARD_DTO_JSON).accept(this::toCard).subscribe();
    }

    private void toCard(CardDTO cardDTO) {
        Type type = TypeExtractor.from(cardDTO.getTypeLine());
        Card card = mappers.get(type.getBasicType()).toCard(cardDTO, type);
        bus.message(Event.CARD_DATA).withContent(card).send();
    }
}
