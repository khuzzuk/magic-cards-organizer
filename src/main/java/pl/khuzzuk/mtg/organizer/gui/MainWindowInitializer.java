package pl.khuzzuk.mtg.organizer.gui;

import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pl.khuzzuk.functions.ForceGate;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.gui.filter.LeftPaneFilter;
import pl.khuzzuk.mtg.organizer.gui.selector.MainViewSelector;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;

import static pl.khuzzuk.mtg.organizer.events.Event.*;

@RequiredArgsConstructor
public class MainWindowInitializer implements Loadable {
    private final Bus<Event> bus;
    private MainWindow window;
    private Stage stage;
    private LeftPaneFilter leftPaneFilter;
    private MainViewSelector mainViewSelector;
    private MenuBar menuBar;

    @Override
    public void load() {
       ForceGate forceGate = ForceGate.of(4, this::onPropertiesSet);
        bus.subscribingFor(SET_PRIMARY_STAGE).onFXThread().<Stage>accept(stage -> {
           this.stage = stage;
           forceGate.on();
        }).subscribe();
        bus.subscribingFor(LEFT_PANE_FILTER).onFXThread().<LeftPaneFilter>accept(leftPaneFilter -> {
                 this.leftPaneFilter = leftPaneFilter;
                 forceGate.on();
              }).subscribe();
        bus.subscribingFor(MAIN_VIEW_SELECTOR).onFXThread().<MainViewSelector>accept(selector -> {
           this.mainViewSelector = selector;
           forceGate.on();
        }).subscribe();
        bus.subscribingFor(ORGANIZER_MENU).onFXThread().<MenuBar>accept(menuBar -> {
           this.menuBar = menuBar;
           forceGate.on();
        }).subscribe();
    }

   private void onPropertiesSet() {
       window = new MainWindow(bus, stage);
       this.window.initialize();
       window.addLeftPaneFilter(leftPaneFilter);
       window.addMainViewSelector(mainViewSelector);
       window.addMenuBar(menuBar);
       bus.message(WINDOW_TO_SHOW).withContent(window).send();
   }
}
