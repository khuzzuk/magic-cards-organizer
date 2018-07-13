package pl.khuzzuk.mtg.organizer;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.gui.MainWindowInitializer;
import pl.khuzzuk.mtg.organizer.gui.card.CardViewer;
import pl.khuzzuk.mtg.organizer.gui.filter.LeftPaneFilter;
import pl.khuzzuk.mtg.organizer.gui.form.Binder;
import pl.khuzzuk.mtg.organizer.gui.menu.OrganizerMenuBar;
import pl.khuzzuk.mtg.organizer.gui.selector.MainViewSelector;
import pl.khuzzuk.mtg.organizer.gui.selector.TableSelector;
import pl.khuzzuk.mtg.organizer.initialize.Container;
import pl.khuzzuk.mtg.organizer.serialization.JsonCardSerializer;
import pl.khuzzuk.mtg.organizer.serialization.JsonCardService;
import pl.khuzzuk.mtg.organizer.serialization.JsonRepoSerializer;
import pl.khuzzuk.mtg.organizer.serialization.ReindexingService;
import pl.khuzzuk.mtg.organizer.settings.SettingsService;

import java.nio.file.Path;

@SpringBootApplication
public class MtgOrganizerApp extends Application {
    private static Bus<Event> bus;

    public static void main(String[] args) {
        SpringApplication.run(MtgOrganizerApp.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        bus.message(Event.SET_PRIMARY_STAGE).withContent(primaryStage).send();
        bus.message(Event.FX_THREAD_STARTED).send();
    }

    public static Container createContainer(Bus<Event> bus, Path repoFile) {
        Container container = new Container(bus);
        createSerialization(container, bus, repoFile);
        createGui(container, bus);
        return container;
    }

    private static void createGui(Container container, Bus<Event> bus) {
        container.prepare(new MainWindowInitializer(bus));
        container.prepare(new LeftPaneFilter(bus));
        container.prepare(new MainViewSelector(bus));
        container.prepare(new CardViewer(bus));
        container.prepare(new TableSelector(bus));
        container.prepare(new OrganizerMenuBar(bus));
        container.prepare(new Binder());
    }

    private static void createSerialization(Container container, Bus<Event> bus, Path repoFile) {
        container.prepare(new JsonCardSerializer(bus));
        container.prepare(new JsonRepoSerializer(bus, repoFile));
        container.prepare(new JsonCardService(bus));
        container.prepare(new SettingsService(bus));
        container.prepare(new ReindexingService(bus));
    }
}
