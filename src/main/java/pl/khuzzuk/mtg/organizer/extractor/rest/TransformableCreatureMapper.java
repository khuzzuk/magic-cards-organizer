package pl.khuzzuk.mtg.organizer.extractor.rest;

import pl.khuzzuk.mtg.organizer.extractor.SkillExtractor;
import pl.khuzzuk.mtg.organizer.extractor.TypeExtractor;
import pl.khuzzuk.mtg.organizer.extractor.rest.data.CardDTO;
import pl.khuzzuk.mtg.organizer.extractor.rest.data.CardFaceDTO;
import pl.khuzzuk.mtg.organizer.model.card.TransformableCreatureCard;
import pl.khuzzuk.mtg.organizer.model.type.Type;

import java.util.Objects;

public class TransformableCreatureMapper extends CreatureCardMapper<TransformableCreatureCard> {
    public TransformableCreatureMapper(SkillExtractor skillExtractor) {
        super(skillExtractor);
    }

    @Override
    public TransformableCreatureCard toCard(CardDTO cardDTO, Type type) {
        TransformableCreatureCard card = super.toCard(cardDTO, type);

        CardFaceDTO front = cardDTO.getCardFaces().get(0);
        card.setName(front.getName());
        card.setManaCost(manaCostFrom(front.getManaCost()));
        card.setAttack(front.getPower());
        card.setDefense(front.getToughness());

        CardFaceDTO back = cardDTO.getCardFaces().get(1);
        card.setTransformedName(back.getName());
        card.setTransformedType(TypeExtractor.from(back));
        card.setTransformedAttack(back.getPower());
        card.setTransformedDefense(back.getToughness());

        cardDTO.getCardFaces().stream()
                .map(CardFaceDTO::getFlavorText)
                .filter(Objects::nonNull)
                .findAny().ifPresent(card::setText);

        return card;
    }

    @Override
    void applyImageUris(CardDTO cardDTO, TransformableCreatureCard card) {
        CardFaceDTO face1 = cardDTO.getCardFaces().get(0);
        CardFaceDTO face2 = cardDTO.getCardFaces().get(1);
        card.setFront(urlFrom(face1.getImageUris().getPng()));
        card.setArt(urlFrom(face1.getImageUris().getArtCrop()));
        card.setBack(urlFrom(face2.getImageUris().getPng()));
        card.setBackArt(urlFrom(face2.getImageUris().getArtCrop()));
    }

    @Override
    void applySkills(CardDTO cardDTO, TransformableCreatureCard card) {
        card.setSkills(skillsFrom(cardDTO.getCardFaces().get(0).getOracleText()));
        card.setTransformedSkills(skillsFrom(cardDTO.getCardFaces().get(1).getOracleText()));
    }

    @Override
    void applyManaCost(CardDTO cardDTO, TransformableCreatureCard card) {
        card.setManaCost(manaCostFrom(cardDTO.getCardFaces().get(0).getManaCost()));
    }
}
