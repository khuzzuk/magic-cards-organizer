package pl.khuzzuk.mtg.organizer.web.bottom;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.web.initialize.ComponentInitialization;
import pl.khuzzuk.mtg.organizer.web.initialize.UIProperty;

import java.net.MalformedURLException;
import java.net.URL;

@RequiredArgsConstructor
@Component
@UIScope
@StyleSheet("styles/mtg-organizer.css")
public class StatusBar extends Div implements InitializingBean {
    private final Bus<Event> bus;
    @UIProperty
    private TextField urlField = new TextField();
    @UIProperty
    private Text label = new Text("empty");

    @Override
    public void afterPropertiesSet() {
        setId("status-bar");
        setClassName("status-class");
        ComponentInitialization.initializeComponents(this);
        urlField.addKeyDownListener(Key.ENTER, event -> sendUrl());
    }

    private void sendUrl() {
        try {
            URL link = new URL(urlField.getValue());
            bus.message(Event.URL_TO_IMPORT).withContent(link).send();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}