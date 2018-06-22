package pl.khuzzuk.mtg.organizer.extractor;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.khuzzuk.mtg.organizer.extractor.rest.CardDTO;
import pl.khuzzuk.mtg.organizer.extractor.rest.CardFaceDTO;
import pl.khuzzuk.mtg.organizer.model.ManaType;
import pl.khuzzuk.mtg.organizer.model.type.BasicType;
import pl.khuzzuk.mtg.organizer.model.type.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class TypeExtractor {
    public static Type from(String line) {
        Type type = new Type();
        String[] lineElements = StringUtils.split(line);
        List<String> left = new ArrayList<>();
        List<String> currentList = left;

        for (String lineElement : lineElements) {
            if (lineElement.length() == 1) {
                currentList = type.getSecondaryTypes();
            } else {
                currentList.add(lineElement);
            }
        }

        type.setBasicType(BasicType.valueOf(left.get(left.size() - 1)));
        left.remove(left.size() - 1);
        type.getPrimaryTypes().addAll(left);

        return type;
    }

    public static Type from(CardDTO cardDTO) {
        Type type;
        if (cardDTO.getCardFaces().size() > 1) {
            type = from(cardDTO.getCardFaces().get(0));
        } else {
            type = from(cardDTO.getTypeLine());
            type.getColors().addAll(colorsFrom(cardDTO.getColorIdentity()));
        }
        return type;
    }

    public static Type from(CardFaceDTO cardFaceDTO) {
        Type type;
        type = from(cardFaceDTO.getTypeLine());
        type.getColors().addAll(colorsFrom(cardFaceDTO.getColors()));
        type.setBasicType(BasicType.TransformableCreature);
        return type;
    }

    private Set<ManaType> colorsFrom(String[] colorsIdentity) {
        return TypeExtractor.getColors(colorsIdentity);
    }

    public static Set<ManaType> getColors(String[] colors) {
        return Arrays.stream(colors).map(TypeExtractor::getManaTypeFromLabel).collect(Collectors.toSet());
    }

    private static ManaType getManaTypeFromLabel(String label) {
        if ("W".equals(label)) return ManaType.WHITE;
        if ("G".equals(label)) return ManaType.GREEN;
        if ("U".equals(label)) return ManaType.BLUE;
        if ("R".equals(label)) return ManaType.RED;
        if ("B".equals(label)) return ManaType.BLACK;
        return ManaType.GENERIC;
    }

    public static Type extractTypeFrom(Element profile) {
        Elements typeLine = profile.getElementsByClass("card-text-type-line");
        Type type = extractType(profile, 0);
        if (type.getBasicType() == BasicType.Creature && typeLine.size() > 1) {
            type.setBasicType(BasicType.TransformableCreature);
        } else if (type.getBasicType() == BasicType.Land &&
                !type.getPrimaryTypes().isEmpty() &&
                "Basic".equalsIgnoreCase(type.getPrimaryTypes().get(0))) {
            type.setBasicType(BasicType.BasicLand);
        }
        return type;
    }

    public static Type extractTransformedTypeFrom(Element profile) {
        Type type = extractType(profile, 1);
        type.setBasicType(BasicType.TransformableCreature);
        return type;
    }

    private static Type extractType(Element profile, int pos) {
        Element typeElement = profile.getElementsByClass("card-text-type-line").get(pos);
        String typeText = typeElement.ownText();
        Type type = from(typeText);

        if (!typeElement.children().isEmpty()) {
            type.getColors().add(ManaType.valueOf(StringUtils.substringAfterLast(typeElement.child(0).text(), " ").toUpperCase()));
        }

        return type;
    }
}
