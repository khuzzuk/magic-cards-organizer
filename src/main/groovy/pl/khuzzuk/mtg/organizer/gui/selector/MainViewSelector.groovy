package pl.khuzzuk.mtg.organizer.gui.selector

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.SplitPane
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.initialize.Identification
import pl.khuzzuk.mtg.organizer.initialize.Loadable

import static pl.khuzzuk.mtg.organizer.Event.MAIN_VIEW_SELECTOR

@Identification(MAIN_VIEW_SELECTOR)
class MainViewSelector extends SplitPane implements Loadable {
    private Bus<Event> bus

    MainViewSelector(Bus<Event> bus) {
        this.bus = bus
    }

    @Override
    void load() {
        bus.subscribingFor(Event.CARD_VIEWER).onFXThread().accept({items.add(it as Node)}).subscribe()
        bus.subscribingFor(Event.TABLE_SELECTOR).onFXThread().accept({items.add(0, it as Node)}).subscribe()
        setOrientation(Orientation.VERTICAL)
        setDividerPosition(0, 0.3d)
    }
}
