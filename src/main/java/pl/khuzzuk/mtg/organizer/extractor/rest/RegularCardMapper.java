package pl.khuzzuk.mtg.organizer.extractor.rest;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pl.khuzzuk.mtg.organizer.extractor.SkillExtractor;
import pl.khuzzuk.mtg.organizer.model.card.RegularCard;
import pl.khuzzuk.mtg.organizer.model.skill.Skill;
import pl.khuzzuk.mtg.organizer.model.type.Type;

import java.util.List;

@AllArgsConstructor
public class RegularCardMapper<T extends RegularCard> extends CardMapper<T> {
    private SkillExtractor skillExtractor;

    @SuppressWarnings("unchecked")
    public T toCard(CardDTO cardDTO, Type type) {
        T card = super.toCard(cardDTO, type);
        card.setSkills(skillsFrom(cardDTO.getOracleText()));
        card.setText(cardDTO.getFlavorText());
        return card;
    }

    List<Skill> skillsFrom(String skillLine) {
        List<String> skillsList = List.of(StringUtils.split(skillLine, "\n"));
        return skillExtractor.extractSkillsFrom(skillsList);
    }
}
