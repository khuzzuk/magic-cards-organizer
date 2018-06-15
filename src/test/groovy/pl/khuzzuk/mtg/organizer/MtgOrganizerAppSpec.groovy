package pl.khuzzuk.mtg.organizer

import javafx.stage.Stage
import org.testfx.api.FxToolkit
import pl.khuzzuk.messaging.Bus

class MtgOrganizerAppSpec extends JavaFxSpecification {
    Bus<Event> bus
    void setup() {
        bus = Bus.initializeBus(Event.class)
        MtgOrganizerApp.createContainer(bus)
    }

    @Override
    void start(Stage stage) throws Exception {
        FxToolkit.registerStage({stage})
    }
}
