package pl.khuzzuk.mtg.organizer.gui.selector;

import javafx.scene.control.TableView;
import pl.khuzzuk.mtg.organizer.initialize.Identification;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;
import pl.khuzzuk.mtg.organizer.model.card.Card;

import static pl.khuzzuk.mtg.organizer.Event.TABLE_SELECTOR;

@Identification(TABLE_SELECTOR)
public class TableSelector extends TableView<Card> implements Loadable {
    @Override
    public void load() {
    }
}
