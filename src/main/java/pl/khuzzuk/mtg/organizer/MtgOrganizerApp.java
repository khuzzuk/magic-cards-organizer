package pl.khuzzuk.mtg.organizer;

import javafx.application.Application;
import javafx.stage.Stage;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.extractor.rest.CardJSONConverter;
import pl.khuzzuk.mtg.organizer.extractor.rest.ScryfallClient;
import pl.khuzzuk.mtg.organizer.gui.MainWindow;
import pl.khuzzuk.mtg.organizer.gui.MainWindowInitializer;
import pl.khuzzuk.mtg.organizer.gui.card.CardViewer;
import pl.khuzzuk.mtg.organizer.gui.filter.LeftPaneFilter;
import pl.khuzzuk.mtg.organizer.gui.form.Binder;
import pl.khuzzuk.mtg.organizer.gui.menu.OrganizerMenuBar;
import pl.khuzzuk.mtg.organizer.gui.selector.MainViewSelector;
import pl.khuzzuk.mtg.organizer.gui.selector.TableSelector;
import pl.khuzzuk.mtg.organizer.initialize.Container;
import pl.khuzzuk.mtg.organizer.serialization.JsonSerializer;
import pl.khuzzuk.mtg.organizer.serialization.PredefinedSkillRepo;

public class MtgOrganizerApp extends Application {
    private static Bus<Event> bus;

    public static void main(String[] args) {
        bus = Bus.initializeBus(Event.class, System.out, true);
        bus.subscribingFor(Event.CLOSE).then(bus::closeBus).subscribe();
        Container container = createContainer(bus);
        bus.subscribingFor(Event.FX_THREAD_STARTED).then(container::sealContainer).subscribe();
        bus.subscribingFor(Event.WINDOW_TO_SHOW).onFXThread().accept(MainWindow::show).subscribe();
        Application.launch(MtgOrganizerApp.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        bus.message(Event.SET_PRIMARY_STAGE).withContent(primaryStage).send();
        bus.message(Event.FX_THREAD_STARTED).send();
    }

    public static Container createContainer(Bus<Event> bus) {
        Container container = new Container(bus);
        createCardDownloaders(container, bus);
        createSerialization(container, bus);
        createGui(container, bus);
        return container;
    }

    private static void createGui(Container container, Bus<Event> bus) {
        container.prepare(new MainWindowInitializer(bus));
        container.prepare(new LeftPaneFilter());
        container.prepare(new MainViewSelector(bus));
        container.prepare(new CardViewer(bus));
        container.prepare(new TableSelector());
        container.prepare(new OrganizerMenuBar(bus));
        container.prepare(new Binder());
    }

    private static void createCardDownloaders(Container container, Bus<Event> bus) {
        container.prepare(new CardJSONConverter(bus));
        container.prepare(new ScryfallClient(bus));
    }

    private static void createSerialization(Container container, Bus<Event> bus) {
        container.prepare(new JsonSerializer(bus));
        container.prepare(new PredefinedSkillRepo(bus));
    }
}
