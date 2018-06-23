package pl.khuzzuk.mtg.organizer.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Mana {
    private int value;
    @NonNull
    private ManaType type;

    public Mana plus(Mana toAdd) {
        if (!type.equals(toAdd.type)) {
            throw new IllegalArgumentException("cannot add different types of mana");
        }

        Mana mana = new Mana(type);
        mana.value = value + toAdd.value;
        return mana;
    }
}
