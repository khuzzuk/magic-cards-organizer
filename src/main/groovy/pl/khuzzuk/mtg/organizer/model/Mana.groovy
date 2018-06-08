package pl.khuzzuk.mtg.organizer.model

class Mana {
    int value
    ManaType type

    Mana(ManaType type) {
        this.type = type
    }



    Mana plus(Mana toAdd) {
        if (type != toAdd.type) {
            throw new IllegalArgumentException('cannot add different types of mana')
        }
        def mana = new Mana(type)
        mana.value = value + toAdd.value
        mana
    }
}
