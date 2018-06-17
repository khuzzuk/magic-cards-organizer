package pl.khuzzuk.mtg.organizer.gui.filter;

import static pl.khuzzuk.mtg.organizer.Event.LEFT_PANE_FILTER;

import javafx.scene.control.ListView;
import pl.khuzzuk.mtg.organizer.initialize.Identification;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;

@Identification(LEFT_PANE_FILTER)
public class LeftPaneFilter extends ListView<String> implements Loadable {
    @Override
    public void load(){
    }
}
