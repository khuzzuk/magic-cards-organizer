package pl.khuzzuk.mtg.organizer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManaCost {
    private Mana generic = new Mana(ManaType.GENERIC);
    private Mana white = new Mana(ManaType.WHITE);
    private Mana green = new Mana(ManaType.GREEN);
    private Mana blue = new Mana(ManaType.BLUE);
    private Mana red = new Mana(ManaType.RED);
    private Mana black = new Mana(ManaType.BLACK);
    private Mana colorless = new Mana(ManaType.COLORLESS);

    public void add(Mana mana) {
        mana.getType().apply(this, mana);
    }
}
