package pl.khuzzuk.mtg.organizer.model.type;

import lombok.AllArgsConstructor;
import pl.khuzzuk.mtg.organizer.model.card.*;

import java.util.function.Supplier;

@AllArgsConstructor
public enum BasicType {
    Land(LandCard::new),
    BasicLand(BasicLandCard::new),
    Creature(CreatureCard::new),
    TransformableCreature(TransformableCreatureCard::new),
    Sorcery(SorceryCard::new),
    Enchantment(EnchantmentCard::new),
    Instant(InstantCard::new),
    Artifact(ArtifactCard::new),
    Planeswalker(PlaneswalkerCard::new);

    private Supplier<? extends Card> cardSupplier;

    public Card createNewCard() {
        return cardSupplier.get();
    }
}
