package pl.khuzzuk.mtg.organizer.extractor;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.khuzzuk.mtg.organizer.model.ManaType;
import pl.khuzzuk.mtg.organizer.model.type.BasicType;
import pl.khuzzuk.mtg.organizer.model.type.Type;

class TypeExtractor {
   static Type from(String line) {
      Type type = new Type();
      String[] lineElements = StringUtils.split(line);
      List<String> left = new LinkedList<>();
      List<String> currentList = left;

      for (String lineElement : lineElements) {
         if (lineElement.length() == 1) {
            currentList = type.getSecondaryTypes();
         } else {
            currentList.add(lineElement);
         }
      }

      if (left.size() > 1) {
         type.getPrimaryTypes().add(left.get(0));
         type.setBasicType(BasicType.valueOf(left.get(1)));
      } else {
         type.setBasicType(BasicType.valueOf(left.get(0)));
      }

      return type;
   }

   static Type extractTypeFrom(Element profile) {
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

   static Type extractTransformedTypeFrom(Element profile) {
      Type type = extractType(profile, 1);
      type.setBasicType(BasicType.TransformableCreature);
      return type;
   }

   static private Type extractType(Element profile, int pos) {
      Element typeElement = profile.getElementsByClass("card-text-type-line").get(pos);
      String typeText = typeElement.ownText();
      Type type = from(typeText);

      if (!typeElement.children().isEmpty()) {
         type.getColors().add(ManaType.valueOf(StringUtils.substringAfterLast(typeElement.child(0).text(), " ").toUpperCase()));
      }

      return type;
   }
}
