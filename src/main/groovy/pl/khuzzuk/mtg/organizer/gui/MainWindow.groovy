package pl.khuzzuk.mtg.organizer.gui

import javafx.scene.Scene
import javafx.scene.control.MenuBar
import javafx.scene.control.SplitPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.gui.filter.LeftPaneFilter
import pl.khuzzuk.mtg.organizer.gui.selector.MainViewSelector

class MainWindow extends Stage {
    private Bus<Event> bus
    private VBox root
    private SplitPane mainPane

    MainWindow(Bus<Event> bus, Stage stage) {
        super(StageStyle.DECORATED)
        initOwner(stage)
        this.bus = bus
    }

    void initialize() {
        setMaximized(true)
        prepareRoot()
        scene = new Scene(root)
        setOnCloseRequest({bus.message(Event.CLOSE).send()})
    }

    private void prepareRoot() {
        root = new VBox()
        mainPane = new SplitPane()
        mainPane.dividerPositions = 0.3
        VBox.setVgrow(mainPane, Priority.ALWAYS)
        root.children.add(mainPane)
    }

    void addLeftPaneFilter(LeftPaneFilter leftPaneFilter) {
        mainPane.items.add(0, leftPaneFilter)
    }

    void addMainViewSelector(MainViewSelector mainViewSelector) {
        mainPane.items.add(mainViewSelector)
    }

    void addMenuBar(MenuBar menuBar) {
        root.children.add(0, menuBar)
    }
}
