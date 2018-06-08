package pl.khuzzuk.mtg.organizer.model

enum ManaType {
    GENERIC {
        @Override
        void apply(ManaCost cost, Mana mana) {
            cost.generic += mana
        }
    },
    WHITE {
        @Override
        void apply(ManaCost cost, Mana mana) {
            cost.white += mana
        }
    },
    GREEN {
        @Override
        void apply(ManaCost cost, Mana mana) {
            cost.green += mana
        }
    },
    BLUE {
        @Override
        void apply(ManaCost cost, Mana mana) {
            cost.blue += mana
        }
    },
    RED {
        @Override
        void apply(ManaCost cost, Mana mana) {
            cost.red += mana
        }
    },
    BLACK {
        @Override
        void apply(ManaCost cost, Mana mana) {
            cost.black += mana
        }
    },
    COLORLESS {
        @Override
        void apply(ManaCost cost, Mana mana) {
            cost.colorless += mana
        }
    }

    abstract void apply(ManaCost cost, Mana mana)
}