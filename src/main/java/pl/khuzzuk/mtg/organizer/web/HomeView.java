package pl.khuzzuk.mtg.organizer.web;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.web.bottom.StatusBar;
import pl.khuzzuk.mtg.organizer.web.card.CardViewer;
import pl.khuzzuk.mtg.organizer.web.find.CardsTable;
import pl.khuzzuk.mtg.organizer.web.find.SetSelector;
import pl.khuzzuk.mtg.organizer.web.initialize.CSS;
import pl.khuzzuk.mtg.organizer.web.initialize.ComponentInitialization;
import pl.khuzzuk.mtg.organizer.web.initialize.UIProperty;

@RequiredArgsConstructor
@Route("")
@Component
@UIScope
@StyleSheet("css/mtg-organizer.css")
@Push
@Tag("HomeView")
public class HomeView extends WebComponent implements InitializingBean {
    private final Bus<Event> bus;

    @UIProperty
    private final CardViewer cardViewer;
    @UIProperty
    @CSS(className = "selection-section")
    private Div selectionSection = new Div();
    @UIProperty
    private final StatusBar statusBar;

    private final SetSelector setSelector;
    private final CardsTable cardsTable;


    @Override
    public void afterPropertiesSet() {
        prepareView();
        bus.subscribingFor(Event.ERROR).accept(this::showNotification).subscribe();
    }

    private void prepareView() {
        ComponentInitialization.initializeComponents(this);
        selectionSection.add(setSelector, cardsTable);
    }

    private void showNotification(String message) {
        execute(() -> new Notification(message, 3000, Notification.Position.BOTTOM_CENTER).open());
    }
}
