package pl.khuzzuk.mtg.organizer.gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.StatusBar;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.gui.filter.LeftPaneFilter;
import pl.khuzzuk.mtg.organizer.gui.selector.MainViewSelector;

import java.net.MalformedURLException;
import java.net.URL;

public class MainWindow extends Stage {
    private Bus<Event> bus;
    private BorderPane root;
    private SplitPane mainPane;
    private Label errorMessage;
    private TextField url;

    public MainWindow(Bus<Event> bus, Stage stage) {
        super(StageStyle.DECORATED);
        initOwner(stage);
        this.bus = bus;
    }

    public void initialize() {
        setMaximized(true);
        prepareRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("mtg.css");
        setScene(scene);
        setOnCloseRequest(event -> bus.message(Event.CLOSE).send());
    }

    private void prepareRoot()
    {
        root = new BorderPane();
        mainPane = new SplitPane();
        mainPane.setDividerPositions(0.3);
        root.setCenter(mainPane);

        StatusBar statusBar = new StatusBar();
        url = new TextField();
        url.getStyleClass().add("url-field");
        url.setOnKeyPressed(event -> importFromUrl());

        Button importUrl = new Button("Import");
        importUrl.setId("import");
        importUrl.setOnAction(event -> importFromUrl());

        errorMessage = new Label();
        statusBar.getRightItems().addAll(errorMessage, this.url, importUrl);
        root.setBottom(statusBar);
    }

    public void addLeftPaneFilter(LeftPaneFilter leftPaneFilter) {
        mainPane.getItems().add(0, leftPaneFilter);
    }

    public void addMainViewSelector(MainViewSelector mainViewSelector) {
        mainPane.getItems().add(mainViewSelector);
    }

    public void addMenuBar(MenuBar menuBar) {
        root.setTop(menuBar);
    }

    private void importFromUrl() {
        try {
            errorMessage.setText(null);
            URL link = new URL(url.getText());
            bus.message(Event.CARD_FROM_URL).withContent(link.toString()).send();
        } catch (MalformedURLException e) {
            errorMessage.setText(e.getMessage());
        }
    }
}
