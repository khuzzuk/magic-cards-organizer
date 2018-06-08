package pl.khuzzuk.mtg.organizer.gui.filter

import javafx.scene.control.ListView
import pl.khuzzuk.mtg.organizer.initialize.Identification
import pl.khuzzuk.mtg.organizer.initialize.Loadable

import static pl.khuzzuk.mtg.organizer.Event.LEFT_PANE_FILTER

@Identification(LEFT_PANE_FILTER)
class LeftPaneFilter extends ListView<String> implements Loadable {
    @Override
    void load() {

    }
}
