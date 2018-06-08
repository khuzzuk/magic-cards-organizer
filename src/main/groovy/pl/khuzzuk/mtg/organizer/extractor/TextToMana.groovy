package pl.khuzzuk.mtg.organizer.extractor

import pl.khuzzuk.mtg.organizer.model.Mana
import pl.khuzzuk.mtg.organizer.model.ManaCost
import pl.khuzzuk.mtg.organizer.model.ManaType

class TextToMana {
    static ManaCost from(String text) {
        if (!text.contains('{')) {
            Collections.emptyList()
        }
        ManaCost manaCost = new ManaCost()
        Arrays.stream(text.split('}'))
                .map({it.replace('{', '')})
                .map({manaFrom(it as String)})
                .forEach({manaCost.add(it)})
        manaCost
    }

    private static Mana manaFrom(String text) {
        Mana mana = new Mana()
        mana.value = 1
        switch (text) {
            case 'W':
                mana.type = ManaType.WHITE
                break
            case 'G':
                mana.type = ManaType.WHITE
                break
            default:
                mana.type = ManaType.GENERIC
                mana.value = Integer.parseInt(text)
                break
        }
        mana
    }
}
