package pl.khuzzuk.mtg.organizer.web.card;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.gui.form.Binder;
import pl.khuzzuk.mtg.organizer.model.card.CreatureCard;
import pl.khuzzuk.mtg.organizer.web.initialize.ComponentInitialization;
import pl.khuzzuk.mtg.organizer.web.initialize.UIProperty;

@RequiredArgsConstructor
@Component
@UIScope
@Tag("CardViewer")
public class CardViewer extends Div implements InitializingBean {
    private final Bus<Event> bus;
    private final Binder binder;

    @UIProperty
    private Label cardName = new Label();

    @Override
    public void afterPropertiesSet() {
        ComponentInitialization.initializeComponents(this);
        binder.bind(CreatureCard.class, this.getClass());
    }
}
