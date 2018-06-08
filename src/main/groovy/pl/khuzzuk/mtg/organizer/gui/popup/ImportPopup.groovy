package pl.khuzzuk.mtg.organizer.gui.popup

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Stage
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.initialize.Identification
import pl.khuzzuk.mtg.organizer.initialize.Loadable

@Identification(Event.IMPORT_POPUP)
class ImportPopup implements Loadable {
    private Bus<Event> bus
    private Stage popup
    private TextField link
    private Label wrongLink
    private VBox frame

    ImportPopup(Bus<Event> bus) {
        this.bus = bus
    }

    @Override
    void load() {
        bus.subscribingFor(Event.FX_THREAD_STARTED).onFXThread().then({initialize()}).subscribe()
    }

    private void initialize() {
        popup = new Stage()
        popup.setTitle('Import from url')

        Button importButton = new Button('Import')
        importButton.setOnAction({importUrl()})
        link = new TextField()
        frame = new VBox()
        frame.children.addAll(link, importButton)

        wrongLink = new Label('Wrong link')
        wrongLink.styleClass.add('alert')

        popup.setScene(new Scene(this.frame))
    }

    void showPopup() {
        link.clear()
        frame.children.remove(wrongLink)
        popup.show()
    }

    void importUrl() {
        try {
            URL url = new URL(link.text)
            bus.message(Event.CARD_FROM_URL).withContent(url.toString()).send()
        } catch (MalformedURLException e) {
            wrongLink.text = e.message
            frame.children.add(wrongLink)
        }
    }
}
