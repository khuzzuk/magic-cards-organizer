package pl.khuzzuk.mtg.organizer.gui.card

import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.gui.ClearFormUtil
import pl.khuzzuk.mtg.organizer.gui.Clearable
import pl.khuzzuk.mtg.organizer.initialize.Identification
import pl.khuzzuk.mtg.organizer.initialize.Loadable
import pl.khuzzuk.mtg.organizer.model.Card
import pl.khuzzuk.mtg.organizer.model.CreatureCard
import pl.khuzzuk.mtg.organizer.model.SpellCard

@Identification(Event.CARD_VIEWER)
class CardViewer extends GridPane implements Loadable {
    private Bus<Event> bus
    @Clearable
    private Label name
    @Clearable
    private Label text
    private ImageView genericIco
    @Clearable('0')
    private Label generic
    private ImageView whiteIco
    @Clearable('0')
    private Label white
    private ImageView greenIco
    @Clearable('0')
    private Label green
    private ImageView blueIco
    @Clearable('0')
    private Label blue
    private ImageView redIco
    @Clearable('0')
    private Label red
    private ImageView blackIco
    @Clearable('0')
    private Label black
    private ImageView colorlessIco
    @Clearable('0')
    private Label colorless
    @Clearable(visibleOnClear = false, value = 'Attack')
    private Label attackLabel
    @Clearable('')
    private Label attack
    @Clearable(visibleOnClear = false, value = 'Defense')
    private Label defenseLabel
    @Clearable('')
    private Label defense
    @Clearable
    private ImageView front

    CardViewer(Bus<Event> bus) {
        this.bus = bus
    }

    @Override
    void load() {
        bus.subscribingFor(Event.CARD_DATA).onFXThread().accept({loadCard(it as Card)}).subscribe()

        styleClass.add('card-viewer')
        setHgap(5)
        setVgap(5)

        name = new Label()
        name.styleClass.add('card-title')
        text = new Label()
        text.styleClass.add('card-lore-text')
        front = new ImageView()
        front.fitWidth = 300
        front.setPreserveRatio(true)
        genericIco = new ImageView('Mana_N.png')
        applyStyleToImageView(genericIco)
        generic = new Label()
        whiteIco = new ImageView('Mana_W.png')
        applyStyleToImageView(whiteIco)
        white = new Label()
        greenIco = new ImageView('Mana_G.png')
        applyStyleToImageView(greenIco)
        green = new Label()
        blueIco = new ImageView('Mana_U.png')
        applyStyleToImageView(blueIco)
        blue = new Label()
        redIco = new ImageView('Mana_R.png')
        applyStyleToImageView(redIco)
        red = new Label()
        blackIco = new ImageView('Mana_B.png')
        applyStyleToImageView(blackIco)
        black = new Label()
        colorlessIco = new ImageView('Mana_N.png')
        applyStyleToImageView(colorlessIco)
        colorless = new Label()
        attackLabel = new Label()
        attack = new Label()
        defenseLabel = new Label()
        defense = new Label()

        add(name, 0, 0, 3, 1)
        add(text, 1, 1, 2, 1)
        add(genericIco, 1, 2)
        add(generic, 2, 2)
        add(whiteIco, 1, 3)
        add(white, 2, 3)
        add(greenIco, 1, 4)
        add(green, 2, 4)
        add(blueIco, 1, 5)
        add(blue, 2, 5)
        add(redIco, 1, 6)
        add(red, 2, 6)
        add(blackIco, 1, 7)
        add(black, 2, 7)
        add(colorlessIco, 1, 8)
        add(colorless, 2, 8)
        add(attackLabel, 1, 9)
        add(attack, 2, 9)
        add(defenseLabel, 1, 10)
        add(defense, 2, 10)
        add(front, 0, 1, 1, 11)
    }

    private void loadCard(Card card) {
        ClearFormUtil.clear(this)
        name.text = card.name
        text.text = card.text

        if (card instanceof SpellCard) {
            def manaCost = card.manaCost
            generic.text = manaCost.generic.value as String
            white.text = manaCost.white.value as String
            green.text = manaCost.green.value as String
            blue.text = manaCost.blue.value as String
            red.text = manaCost.red.value as String
            black.text = manaCost.black.value as String
            colorless.text = manaCost.colorless.value as String
            genericIco.visible = true
            whiteIco.visible = true
            greenIco.visible = true
            blueIco.visible = true
            redIco.visible = true
            blackIco.visible = true
            colorlessIco.visible = true
        }

        if (card instanceof CreatureCard) {
            attackLabel.visible = true
            attack.text = card.defence as String
            attack.visible = true
            defenseLabel.visible = true
            defense.text = card.defence as String
            defense.visible = true
        }

        front.image = new Image(card.front.toString())
    }

    private static void applyStyleToImageView(ImageView imageView) {
        imageView.fitWidth = 20
        imageView.fitHeight = 20
        imageView.setPreserveRatio(true)
        imageView.styleClass.add('mana-icon')
    }
}
