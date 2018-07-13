package pl.khuzzuk.mtg.organizer.web;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.web.bottom.StatusBar;

@Route("")
@Component
@UIScope
@StyleSheet("css/main.css")
public class HomeView extends Div implements InitializingBean {
    private final Bus<Event> bus;

    @Autowired
    public HomeView(StatusBar statusBar, Bus<Event> bus) {
        this.bus = bus;
        add(statusBar);
    }

    @Override
    public void afterPropertiesSet() {
        bus.subscribingFor(Event.ERROR).accept(this::showNotification).subscribe();
    }

    private void showNotification(String message) {
        getUI().get().access(() -> new Notification(message, 3000, Notification.Position.BOTTOM_CENTER).open());
    }
}
