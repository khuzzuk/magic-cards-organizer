package pl.khuzzuk.mtg.organizer.extractor.rest;

import lombok.SneakyThrows;
import pl.khuzzuk.mtg.organizer.model.Rarity;
import pl.khuzzuk.mtg.organizer.model.card.Card;
import pl.khuzzuk.mtg.organizer.model.card.Rule;
import pl.khuzzuk.mtg.organizer.model.type.Type;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static pl.khuzzuk.mtg.organizer.model.Rarity.*;

public abstract class CardMapper<T extends Card> {
    public T toCard(CardDTO cardDTO, Type type) {
        T card = (T) type.getBasicType().createNewCard();

        card.setType(type);
        card.setName(cardDTO.getName());
        card.setRarity(rarityFrom(cardDTO.getRarity()));
        card.setSource(urlFrom(cardDTO.getUri()));
        card.setPrintRef(cardDTO.getSet());
        card.setPrintFullName(cardDTO.getSetName());
        card.setPrintOrder(cardDTO.getCollectorNumber());
        card.setEdhrecRank(cardDTO.getEdhrecRank());
        applyImageUris(cardDTO, card);
        cardDTO.getRulings().getData().stream().map(this::mapRule).forEach(card.getRulings()::add);

        return card;
    }

    void applyImageUris(CardDTO cardDTO, T card) {
        card.setFront(urlFrom(cardDTO.getImageUris().getPng()));
        card.setArt(urlFrom(cardDTO.getImageUris().getArtCrop()));
    }

    @SneakyThrows(MalformedURLException.class)
    URL urlFrom(URI uri) {
        return uri.toURL();
    }

    Rarity rarityFrom(String rarity) {
        if ("mythic".equals(rarity)) return MYTHIC_RARE;
        if ("rare".equals(rarity)) return RARE;
        if ("uncommon".equals(rarity)) return UNCOMMON;
        if ("common".equals(rarity)) return COMMON;
        throw new IllegalArgumentException(rarity);
    }

    private Rule mapRule(RulingsDTO.RuleDTO ruleDTO) {
        Rule rule = new Rule();
        rule.setText(ruleDTO.getComment());
        rule.setPublished(ruleDTO.getPublishedAt());
        rule.setSource(ruleDTO.getSource());
        return rule;
    }
}
