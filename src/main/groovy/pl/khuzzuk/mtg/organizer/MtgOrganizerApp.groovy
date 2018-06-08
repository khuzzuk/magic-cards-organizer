package pl.khuzzuk.mtg.organizer

import javafx.application.Application
import javafx.stage.Stage
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.extractor.HtmlCardExtractor
import pl.khuzzuk.mtg.organizer.gui.card.CardViewer
import pl.khuzzuk.mtg.organizer.gui.filter.LeftPaneFilter
import pl.khuzzuk.mtg.organizer.gui.MainWindowInitializer
import pl.khuzzuk.mtg.organizer.gui.menu.OrganizerMenuBar
import pl.khuzzuk.mtg.organizer.gui.popup.ImportPopup
import pl.khuzzuk.mtg.organizer.gui.selector.MainViewSelector
import pl.khuzzuk.mtg.organizer.gui.selector.TableSelector
import pl.khuzzuk.mtg.organizer.initialize.Container

class MtgOrganizerApp extends Application {
    private static Bus<Event> bus
    static void main(String[] args) {
        bus = Bus.initializeBus(Event.class, System.out, true)
        bus.subscribingFor(Event.CLOSE).then({bus.closeBus()}).subscribe()
        def container = createContainer(bus)
        bus.subscribingFor(Event.FX_THREAD_STARTED).then({container.sealContainer()}).subscribe()
        launch(MtgOrganizerApp.class, args)
    }

    @Override
    void start(Stage primaryStage) throws Exception {
        bus.message(Event.SET_PRIMARY_STAGE).withContent(primaryStage).send()
        bus.message(Event.FX_THREAD_STARTED).send()
    }

    static Container createContainer(Bus<Event> bus) {
        Container container = new Container(bus)
        createHtmlExtraction(container, bus)
        createGui(container, bus)
        container
    }

    static void createGui(Container container, Bus<Event> bus) {
        container.prepare(new MainWindowInitializer(bus))
        container.prepare(new LeftPaneFilter())
        container.prepare(new MainViewSelector(bus))
        container.prepare(new CardViewer(bus))
        container.prepare(new TableSelector())
        container.prepare(new OrganizerMenuBar(bus))
        container.prepare(new ImportPopup(bus))
    }

    static void createHtmlExtraction(Container container, Bus<Event> bus) {
        container.prepare(new HtmlCardExtractor(bus))
    }
}
