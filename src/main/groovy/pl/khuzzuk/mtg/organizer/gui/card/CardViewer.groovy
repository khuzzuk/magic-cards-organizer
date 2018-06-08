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
    @Clearable
    private ImageView front

    CardViewer(Bus<Event> bus) {
        this.bus = bus
    }

    @Override
    void load() {
        bus.subscribingFor(Event.CARD_DATA).onFXThread().accept({loadCard(it as Card)}).subscribe()

        name = new Label()
        text = new Label()
        front = new ImageView()
        genericIco = new ImageView('Mana_N.png')
        generic = new Label()
        whiteIco = new ImageView('Mana_W.png')
        white = new Label()
        greenIco = new ImageView('Mana_G.png')
        green = new Label()
        blueIco = new ImageView('Mana_U.png')
        blue = new Label()
        redIco = new ImageView('Mana_R.png')
        red = new Label()
        blackIco = new ImageView('Mana_B.png')
        black = new Label()
        colorlessIco = new ImageView('Mana_N.png')
        colorless = new Label()
        add(name, 0, 0, 3, 1)
        add(text, 0, 1, 2, 1)
        add(genericIco, 0, 2)
        add(generic, 1, 2)
        add(whiteIco, 0, 3)
        add(white, 1, 3)
        add(greenIco, 0, 4)
        add(green, 1, 4)
        add(blueIco, 0, 5)
        add(blue, 1, 5)
        add(redIco, 0, 6)
        add(red, 1, 6)
        add(blackIco, 0, 7)
        add(black, 1, 7)
        add(colorlessIco, 0, 8)
        add(colorless, 1, 8)
        add(front, 2, 1)
    }

    private void loadCard(Card card) {
        ClearFormUtil.clear(this)
        name.text = card.name
        text.text = card.text

        def manaCost = card.manaCost
        generic.text = manaCost.generic.value as String
        white.text = manaCost.white.value as String
        green.text = manaCost.green.value as String
        blue.text = manaCost.blue.value as String
        red.text = manaCost.red.value as String
        black.text = manaCost.black.value as String
        colorless.text = manaCost.colorless.value as String

        front.image = new Image(card.front.toString())
    }
}
