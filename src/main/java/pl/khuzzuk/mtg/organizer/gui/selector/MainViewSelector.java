package pl.khuzzuk.mtg.organizer.gui.selector;

import static pl.khuzzuk.mtg.organizer.Event.MAIN_VIEW_SELECTOR;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import lombok.AllArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.initialize.Identification;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;

@AllArgsConstructor
@Identification(MAIN_VIEW_SELECTOR)
public class MainViewSelector extends SplitPane implements Loadable {
    private Bus<Event> bus;

    @Override
    public void load() {
        bus.subscribingFor(Event.CARD_VIEWER).onFXThread()
              .<Node>accept(cardViewer -> getItems().add(cardViewer)).subscribe();
        bus.subscribingFor(Event.TABLE_SELECTOR).onFXThread()
              .<Node>accept(tableSelector -> getItems().add(tableSelector)).subscribe();

        setOrientation(Orientation.VERTICAL);
        setDividerPosition(0, 0.3d);
    }
}
