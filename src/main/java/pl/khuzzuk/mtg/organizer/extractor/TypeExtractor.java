package pl.khuzzuk.mtg.organizer.extractor;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import pl.khuzzuk.mtg.organizer.extractor.rest.data.CardDTO;
import pl.khuzzuk.mtg.organizer.extractor.rest.data.CardFaceDTO;
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
        return Arrays.stream(colorsIdentity).map(TypeExtractor::getManaTypeFromLabel).collect(Collectors.toSet());
    }

    private static ManaType getManaTypeFromLabel(String label) {
        if ("W".equals(label)) return ManaType.WHITE;
        if ("G".equals(label)) return ManaType.GREEN;
        if ("U".equals(label)) return ManaType.BLUE;
        if ("R".equals(label)) return ManaType.RED;
        if ("B".equals(label)) return ManaType.BLACK;
        return ManaType.GENERIC;
    }
}
