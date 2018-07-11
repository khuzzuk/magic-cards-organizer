package pl.khuzzuk.mtg.organizer.gui.menu;

import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import lombok.RequiredArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.initialize.Identification;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;

import java.io.File;

@RequiredArgsConstructor
@Identification(Event.ORGANIZER_MENU)
public class OrganizerMenuBar extends MenuBar implements Loadable {
    private final Bus<Event> bus;
    private DirectoryChooser directoryChooser;

    @Override
    public void load() {
        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Cards data directory");

        MenuItem dataLocationSetting = new MenuItem("Data location");
        dataLocationSetting.setOnAction(this::setDataLocation);
        Menu card = new Menu("Card");
        card.getItems().add(dataLocationSetting);
        getMenus().add(card);
    }

    private void setDataLocation(ActionEvent ignore) {
        File dataLocation = directoryChooser.showDialog(null);
        if (dataLocation != null && dataLocation.exists()) {
            bus.message(Event.SET_REPO_LOCATION).withContent(dataLocation.getAbsolutePath()).send();
        }
    }
}
