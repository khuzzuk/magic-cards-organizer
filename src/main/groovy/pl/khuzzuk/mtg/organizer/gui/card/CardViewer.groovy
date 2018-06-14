package pl.khuzzuk.mtg.organizer.gui.card

import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.gui.form.Binder
import pl.khuzzuk.mtg.organizer.gui.form.FormProperty
import pl.khuzzuk.mtg.organizer.gui.form.GridField
import pl.khuzzuk.mtg.organizer.gui.form.GridLayoutController
import pl.khuzzuk.mtg.organizer.gui.form.HideCheck
import pl.khuzzuk.mtg.organizer.initialize.Identification
import pl.khuzzuk.mtg.organizer.initialize.Loadable
import pl.khuzzuk.mtg.organizer.model.Card
import pl.khuzzuk.mtg.organizer.model.CreatureCard

@Identification(Event.CARD_VIEWER)
class CardViewer extends GridPane implements Loadable {
    private Bus<Event> bus
    private Binder binder

    @FormProperty
    @GridField(columnSpan = 3)
    private Label name
    @FormProperty
    @GridField(column = 1, row = 1, columnSpan = 2)
    private Label text

    @HideCheck('generic')
    @GridField(column = 1, row = 2)
    private ImageView genericIco
    @FormProperty(defaultValue = '0', beanPath = 'manaCost.generic.value')
    @GridField(column = 2, row = 2)
    private Label generic
    @HideCheck('white')
    @GridField(column = 1, row = 3)
    private ImageView whiteIco
    @FormProperty(defaultValue = '0', beanPath = 'manaCost.white.value')
    @GridField(column = 2, row = 3)
    private Label white
    @HideCheck('green')
    @GridField(column = 1, row = 4)
    private ImageView greenIco
    @FormProperty(defaultValue = '0', beanPath = 'manaCost.green.value')
    @GridField(column = 2, row = 4)
    private Label green
    @HideCheck('blue')
    @GridField(column = 1, row = 5)
    private ImageView blueIco
    @FormProperty(defaultValue = '0', beanPath = 'manaCost.blue.value')
    @GridField(column = 2, row = 5)
    private Label blue
    @HideCheck('red')
    @GridField(column = 1, row = 6)
    private ImageView redIco
    @FormProperty(defaultValue = '0', beanPath = 'manaCost.red.value')
    @GridField(column = 2, row = 6)
    private Label red
    @HideCheck('black')
    @GridField(column = 1, row = 7)
    private ImageView blackIco
    @FormProperty(defaultValue = '0', beanPath = 'manaCost.black.value')
    @GridField(column = 2, row = 7)
    private Label black
    @HideCheck('colorless')
    @GridField(column = 1, row = 8)
    private ImageView colorlessIco
    @FormProperty(defaultValue = '0', beanPath = 'manaCost.colorless.value')
    @GridField(column = 2, row = 8)
    private Label colorless

    @HideCheck('attack')
    @GridField(column = 1, row = 9)
    private Label attackLabel
    @FormProperty(hideAfterClear = true)
    @GridField(column = 2, row = 9)
    private Label attack
    @HideCheck('defense')
    @GridField(column = 1, row = 10)
    private Label defenseLabel
    @FormProperty(hideAfterClear = true)
    @GridField(column = 2, row = 10)
    private Label defense

    @FormProperty
    @GridField(row = 1, rowSpan = 11)
    private ImageView front

    CardViewer(Bus<Event> bus) {
        this.bus = bus
    }

    @Override
    void load() {
        bus.subscribingFor(Event.BINDER).accept({
            this.binder = it as Binder
            binder.bind(CreatureCard.class, this.class)
            bus.subscribingFor(Event.CARD_DATA).onFXThread().accept({ loadCard(it as Card) }).subscribe()
        }).subscribe()

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
        attackLabel = new Label('attack')
        attack = new Label()
        defenseLabel = new Label('defense')
        defense = new Label()

        GridLayoutController.placeFieldOnGrid(this, this)
    }

    private void loadCard(Card card) {
        binder.clearForm(this)
        binder.fillForm(this, card)
    }

    private static void applyStyleToImageView(ImageView imageView) {
        imageView.fitWidth = 20
        imageView.fitHeight = 20
        imageView.setPreserveRatio(true)
        imageView.styleClass.add('mana-icon')
    }
}
