package pl.khuzzuk.mtg.organizer.gui.selector

import javafx.scene.control.TableView
import pl.khuzzuk.mtg.organizer.initialize.Identification
import pl.khuzzuk.mtg.organizer.initialize.Loadable
import pl.khuzzuk.mtg.organizer.model.Card

import static pl.khuzzuk.mtg.organizer.Event.TABLE_SELECTOR

@Identification(TABLE_SELECTOR)
class TableSelector extends TableView<Card> implements Loadable {
    @Override
    void load() {

    }
}
