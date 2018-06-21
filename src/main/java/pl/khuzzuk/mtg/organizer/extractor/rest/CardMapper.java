package pl.khuzzuk.mtg.organizer.extractor.rest;

import lombok.SneakyThrows;
import pl.khuzzuk.mtg.organizer.extractor.TypeExtractor;
import pl.khuzzuk.mtg.organizer.model.ManaType;
import pl.khuzzuk.mtg.organizer.model.Rarity;
import pl.khuzzuk.mtg.organizer.model.card.Card;
import pl.khuzzuk.mtg.organizer.model.type.Type;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;

import static pl.khuzzuk.mtg.organizer.model.Rarity.*;

public abstract class CardMapper<T extends Card> {
    public T toCard(CardDTO cardDTO, Type type) {
        type.setColors(colorsFrom(cardDTO.getColorIdentity()));

        T card = (T) type.getBasicType().createNewCard();

        card.setType(type);
        card.setName(cardDTO.getName());
        card.setRarity(rarityFrom(cardDTO.getRarity()));
        card.setFront(urlFrom(cardDTO.getImageUris().getPng()));
        card.setArt(urlFrom(cardDTO.getImageUris().getArtCrop()));
        card.setSource(urlFrom(cardDTO.getUri()));
        card.setPrintRef(cardDTO.getSet());
        card.setPrintFullName(cardDTO.getSetName());
        card.setPrintOrder(cardDTO.getCollectorNumber());
        card.setEdhrecRank(cardDTO.getEdhrecRank());

        return card;
    }

    @SneakyThrows(MalformedURLException.class)
    private URL urlFrom(URI uri) {
        return uri.toURL();
    }

    Rarity rarityFrom(String rarity) {
        if ("mythic".equals(rarity)) return MYTHIC_RARE;
        if ("rare".equals(rarity)) return RARE;
        if ("uncommon".equals(rarity)) return UNCOMMON;
        if ("common".equals(rarity)) return COMMON;
        throw new IllegalArgumentException(rarity);
    }

    Set<ManaType> colorsFrom(String[] colorsIdentity) {
        return TypeExtractor.getColors(colorsIdentity);
    }
}
