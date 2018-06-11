package pl.khuzzuk.mtg.organizer.gui

import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.controlsfx.control.StatusBar
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.gui.filter.LeftPaneFilter
import pl.khuzzuk.mtg.organizer.gui.selector.MainViewSelector

class MainWindow extends Stage {
    private Bus<Event> bus
    private BorderPane root
    private SplitPane mainPane
    private Label errorMessage
    private TextField url

    MainWindow(Bus<Event> bus, Stage stage) {
        super(StageStyle.DECORATED)
        initOwner(stage)
        this.bus = bus
    }

    void initialize() {
        setMaximized(true)
        prepareRoot()
        scene = new Scene(root)
        scene.getStylesheets().add('mtg.css')
        setOnCloseRequest({ bus.message(Event.CLOSE).send() })
    }

    private void prepareRoot() {
        root = new BorderPane()
        mainPane = new SplitPane()
        mainPane.dividerPositions = 0.3
        root.setCenter(mainPane)

        StatusBar statusBar = new StatusBar()
        url = new TextField()
        url.styleClass.add('url-field')
        url.onAction = { importFromUrl() }
        Button importUrl = new Button('Import')
        importUrl.onAction = { importFromUrl() }
        errorMessage = new Label()
        statusBar.rightItems.addAll(errorMessage, this.url, importUrl)
        root.setBottom(statusBar)
    }

    void addLeftPaneFilter(LeftPaneFilter leftPaneFilter) {
        mainPane.items.add(0, leftPaneFilter)
    }

    void addMainViewSelector(MainViewSelector mainViewSelector) {
        mainPane.items.add(mainViewSelector)
    }

    void addMenuBar(MenuBar menuBar) {
        root.setTop(menuBar)
    }

    private void importFromUrl() {
        try {
            errorMessage.text = null
            URL link = new URL(url.text)
            bus.message(Event.CARD_FROM_URL).withContent(link.toString()).send()
        } catch (MalformedURLException e) {
            errorMessage.text = e.message
        }
    }
}