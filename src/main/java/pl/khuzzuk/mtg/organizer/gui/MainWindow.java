package pl.khuzzuk.mtg.organizer.gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
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

import static pl.khuzzuk.mtg.organizer.Event.CARD_DATA;

public class MainWindow extends Stage {
    private Bus<Event> bus;
    private BorderPane root;
    private SplitPane mainPane;
    private Label errorMessage;
    private TextField url;
    private ProgressIndicator progressIndicator;

    MainWindow(Bus<Event> bus, Stage stage) {
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

    private void prepareRoot() {
        root = new BorderPane();
        mainPane = new SplitPane();
        mainPane.setDividerPositions(0.3);
        root.setCenter(mainPane);

        StatusBar statusBar = new StatusBar();
        url = new TextField();
        url.getStyleClass().add("url-field");
        url.setOnKeyPressed(this::importFieldKeyEvent);

        Button importUrl = new Button("Import");
        importUrl.setId("import");
        importUrl.setOnAction(event -> importFromUrl());

        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        errorMessage = new Label();
        statusBar.getLeftItems().addAll(progressIndicator, errorMessage);
        statusBar.getRightItems().addAll(this.url, importUrl);
        root.setBottom(statusBar);

        bus.subscribingFor(CARD_DATA).accept(card -> progressIndicator.setVisible(false)).subscribe();
    }

    void addLeftPaneFilter(LeftPaneFilter leftPaneFilter) {
        mainPane.getItems().add(0, leftPaneFilter);
    }

    void addMainViewSelector(MainViewSelector mainViewSelector) {
        mainPane.getItems().add(mainViewSelector);
    }

    void addMenuBar(MenuBar menuBar) {
        root.setTop(menuBar);
    }

    private void importFieldKeyEvent(KeyEvent event) {
        switch (event.getCode()) {
            case ENTER:
                importFromUrl();
                break;
            case ESCAPE:
                errorMessage.setText(null);
                url.setText(null);
                break;
        }
    }

    private void importFromUrl() {
        try {
            errorMessage.setText(null);
            URL link = new URL(url.getText());
            bus.message(Event.CARD_FROM_URL).withContent(link).send();
            progressIndicator.setVisible(true);
        } catch (MalformedURLException e) {
            errorMessage.setText(e.getMessage());
        }
    }
}
