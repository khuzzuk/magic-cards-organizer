package pl.khuzzuk.mtg.organizer.model

class ManaCost {
    Mana generic = new Mana(ManaType.GENERIC)
    Mana white = new Mana(ManaType.WHITE)
    Mana green = new Mana(ManaType.GREEN)
    Mana blue = new Mana(ManaType.BLUE)
    Mana red = new Mana(ManaType.RED)
    Mana black = new Mana(ManaType.BLACK)
    Mana colorless = new Mana(ManaType.COLORLESS)

    void add(Mana mana) {
        mana.type.apply(this, mana)
    }
}
