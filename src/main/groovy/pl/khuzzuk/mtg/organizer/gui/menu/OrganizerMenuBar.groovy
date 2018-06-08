package pl.khuzzuk.mtg.organizer.gui.menu

import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.gui.popup.ImportPopup
import pl.khuzzuk.mtg.organizer.initialize.Identification
import pl.khuzzuk.mtg.organizer.initialize.Loadable

@Identification(Event.ORGANIZER_MENU)
class OrganizerMenuBar extends MenuBar implements Loadable {
    private Bus<Event> bus

    OrganizerMenuBar(Bus<Event> bus) {
        this.bus = bus
    }

    @Override
    void load() {
        MenuItem importCard = new MenuItem('Import')
        Menu card = new Menu('Card')
        card.items.add(importCard)
        menus.add(card)

        bus.subscribingFor(Event.IMPORT_POPUP).accept({showImportPopup(importCard, it as ImportPopup)}).subscribe()
    }

    private static void showImportPopup(MenuItem item, ImportPopup popup) {
        item.setOnAction({popup.showPopup()})
    }
}
