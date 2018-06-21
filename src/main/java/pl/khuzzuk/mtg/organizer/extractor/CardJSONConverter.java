package pl.khuzzuk.mtg.organizer.extractor;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.extractor.rest.CardDTO;
import pl.khuzzuk.mtg.organizer.extractor.rest.RegularCardMapper;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;
import pl.khuzzuk.mtg.organizer.model.card.Card;
import pl.khuzzuk.mtg.organizer.model.type.Type;
import pl.khuzzuk.mtg.organizer.serialization.PredefinedSkillRepo;

@RequiredArgsConstructor
public class CardJSONConverter implements Loadable {
   private final Bus<Event> bus;
   private RegularCardMapper scryfallMapper;

   @Override
   public void load() {
      bus.subscribingFor(Event.PREDEFINED_SKILLS).accept(this::setPredefinedSkillRepo).subscribe();
   }

   private void setPredefinedSkillRepo(PredefinedSkillRepo predefinedSkillRepo) {
      scryfallMapper = Mappers.getMapper(RegularCardMapper.class);
      scryfallMapper.setSkillExtractor(new SkillExtractor(predefinedSkillRepo));
      bus.subscribingFor(Event.CARD_DTO_JSON).accept(this::toCard).subscribe();
   }

   private void toCard(CardDTO cardDTO) {
      Type type = TypeExtractor.from(cardDTO.getTypeLine());
      Card card = null;
      switch (type.getBasicType()) {
         case Land:
            card = scryfallMapper.toCard(cardDTO);
            break;
      }

      card.setType(type);
      bus.message(Event.CARD_DATA).withContent(card).send();
   }
}
