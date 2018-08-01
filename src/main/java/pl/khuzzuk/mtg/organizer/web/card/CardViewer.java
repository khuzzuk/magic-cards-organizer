package pl.khuzzuk.mtg.organizer.web.card;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.binder.Binder;
import pl.khuzzuk.binder.FormProperty;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.model.card.Card;
import pl.khuzzuk.mtg.organizer.model.card.CreatureCard;
import pl.khuzzuk.mtg.organizer.web.WebComponent;
import pl.khuzzuk.mtg.organizer.web.initialize.CSS;
import pl.khuzzuk.mtg.organizer.web.initialize.ComponentInitialization;
import pl.khuzzuk.mtg.organizer.web.initialize.UIProperty;

@RequiredArgsConstructor
@Component
@UIScope
@Tag("CardViewer")
public class CardViewer extends WebComponent implements InitializingBean {
    private final Bus<Event> bus;
    private final Binder binder;

    @UIProperty
    @FormProperty
    @CSS(className = "card-image")
    private Image front = new Image();

    @UIProperty
    @FormProperty
    private Label name = new Label();

    @Override
    public void afterPropertiesSet() {
        ComponentInitialization.initializeComponents(this);
        binder.bind(CreatureCard.class, this.getClass());
        bus.subscribingFor(Event.CARD_DATA).accept(this::showCard).subscribe();
    }

    private void showCard(Card card) {
        execute(() -> {
            binder.clearForm(this);
            binder.fillForm(this, card);
        });
    }
}
