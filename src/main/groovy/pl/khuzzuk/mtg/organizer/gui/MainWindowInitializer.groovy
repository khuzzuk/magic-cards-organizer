package pl.khuzzuk.mtg.organizer.gui

import javafx.scene.control.MenuBar
import javafx.stage.Stage
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.gui.filter.LeftPaneFilter
import pl.khuzzuk.mtg.organizer.gui.selector.MainViewSelector
import pl.khuzzuk.mtg.organizer.initialize.Loadable

import static pl.khuzzuk.mtg.organizer.Event.LEFT_PANE_FILTER
import static pl.khuzzuk.mtg.organizer.Event.MAIN_VIEW_SELECTOR
import static pl.khuzzuk.mtg.organizer.Event.ORGANIZER_MENU
import static pl.khuzzuk.mtg.organizer.Event.SET_PRIMARY_STAGE

class MainWindowInitializer implements Loadable {
    private Bus<Event> bus
    private MainWindow window

    MainWindowInitializer(Bus<Event> bus) {
        this.bus = bus
    }

    @Override
    void load() {
        bus.subscribingFor(SET_PRIMARY_STAGE).onFXThread().accept({loadOnFxThread(it as Stage)}).subscribe()
        bus.subscribingFor(LEFT_PANE_FILTER).onFXThread()
                .accept({window.addLeftPaneFilter(it as LeftPaneFilter)}).subscribe()
        bus.subscribingFor(MAIN_VIEW_SELECTOR).onFXThread()
                .accept({window.addMainViewSelector(it as MainViewSelector)}).subscribe()
        bus.subscribingFor(ORGANIZER_MENU).onFXThread().accept({window.addMenuBar(it as MenuBar)}).subscribe()
    }

    void loadOnFxThread(Stage stage) {
        window = new MainWindow(bus, stage)
        this.window.initialize()
        this.window.show()
    }
}
