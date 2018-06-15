package pl.khuzzuk.mtg.organizer.model;

public enum ManaType {
    GENERIC {
        @Override
        public void apply(ManaCost cost, Mana mana) {
            cost.setGeneric(cost.getGeneric().plus(mana));
        }


    },
    WHITE {
        @Override
        public void apply(ManaCost cost, Mana mana) {
            cost.setWhite(cost.getWhite().plus(mana));
        }


    },
    GREEN {
        @Override
        public void apply(ManaCost cost, Mana mana) {
            cost.setGreen(cost.getGreen().plus(mana));
        }


    },
    BLUE {
        @Override
        public void apply(ManaCost cost, Mana mana) {
            cost.setBlue(cost.getBlue().plus(mana));
        }


    },
    RED {
        @Override
        public void apply(ManaCost cost, Mana mana) {
            cost.setRed(cost.getRed().plus(mana));
        }


    },
    BLACK {
        @Override
        public void apply(ManaCost cost, Mana mana) {
            cost.setBlack(cost.getBlack().plus(mana));
        }


    },
    COLORLESS {
        @Override
        public void apply(ManaCost cost, Mana mana) {
            cost.setColorless(cost.getColorless().plus(mana));
        }


    };

    public abstract void apply(ManaCost cost, Mana mana);
}
