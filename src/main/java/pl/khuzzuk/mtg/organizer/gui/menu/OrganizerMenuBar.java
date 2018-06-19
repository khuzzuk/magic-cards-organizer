package pl.khuzzuk.mtg.organizer.gui.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import lombok.AllArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.initialize.Identification;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;

@AllArgsConstructor
@Identification(Event.ORGANIZER_MENU)
public class OrganizerMenuBar extends MenuBar implements Loadable {
    private Bus<Event> bus;

    @Override
    public void load() {
        MenuItem importCard = new MenuItem("Import");
        Menu card = new Menu("Card");
        card.getItems().add(importCard);
        getMenus().add(card);
    }
}
