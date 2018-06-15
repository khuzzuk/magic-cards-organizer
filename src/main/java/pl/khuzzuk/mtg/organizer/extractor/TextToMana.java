package pl.khuzzuk.mtg.organizer.extractor;

import pl.khuzzuk.mtg.organizer.model.Mana;
import pl.khuzzuk.mtg.organizer.model.ManaCost;
import pl.khuzzuk.mtg.organizer.model.ManaType;

import java.util.Arrays;

public class TextToMana {
    public static ManaCost from(String text) {
        ManaCost manaCost = new ManaCost();
        Arrays.stream(text.contains("{") ? text.split("}") : new String[0])
                .map(t -> t.replace("{", ""))
                .map(TextToMana::manaFrom)
                .forEach(manaCost::add);
        return manaCost;
    }

    private static Mana manaFrom(String text) {
        Mana mana;
        if ("W".equals(text)) {
            mana = new Mana(ManaType.WHITE);
            mana.setValue(1);
        } else if ("G".equals(text)) {
            mana = new Mana(ManaType.GREEN);
            mana.setValue(1);
        } else {
            mana = new Mana(ManaType.GENERIC);
            mana.setValue(Integer.parseInt(text));
        }
        return mana;
    }
}
