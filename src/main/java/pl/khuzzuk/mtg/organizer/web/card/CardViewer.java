package pl.khuzzuk.mtg.organizer.web.card;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.binder.Binder;
import pl.khuzzuk.binder.FormProperty;
import pl.khuzzuk.binder.HideCheck;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.model.card.*;
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

    @FormProperty
    @CSS(className = "card-image")
    private Image front = new Image();
    private Button reverse = new Button("Reverse");

    @FormProperty
    private Label name = new Label();
    @CSS(className = "mana-ico")
    @HideCheck("whiteCost")
    private Image whiteIco = new Image("images/Mana_W.png", "W");
    @FormProperty(beanPath = "manaCost.white.value", hideAfterClear = true)
    private Label whiteCost = new Label();

    @UIProperty
    @CSS(className = "card-main-flex")
    private FlexLayout main = new FlexLayout();
    @CSS(className = "card-left-flex")
    private FlexLayout left = new FlexLayout();
    @CSS(className = "card-middle-flex")
    private FlexLayout middle = new FlexLayout();
    private Card bean;

    private Runnable onImageClick;

    @Override
    public void afterPropertiesSet() {
        ComponentInitialization.initializeComponents(this);
        positionElements();
        reverse.addClickListener(event -> onImageClick.run());

        binder.bind(CreatureCard.class, this.getClass());
        binder.bind(TransformableCreatureCard.class, this.getClass());
        binder.bind(SorceryCard.class, this.getClass());
        binder.bind(InstantCard.class, this.getClass());
        binder.bind(ArtifactCard.class, this.getClass());
        binder.bind(EnchantmentCard.class, this.getClass());
        binder.bind(PlaneswalkerCard.class, this.getClass());
        binder.bind(LandCard.class, this.getClass());
        binder.bind(BasicLandCard.class, this.getClass());
        bus.subscribingFor(Event.CARD_DATA).accept(this::showCard).subscribe();

        refreshActions();
    }

    private void positionElements() {
        main.add(left, middle);
        left.add(name, whiteIco);
        middle.add(front, reverse);
        reverse.setVisible(false);
    }

    private void showCard(Card card) {
        bean = card;
        execute(() -> {
            refreshActions();
            binder.fillForm(this, card);
        });
    }

    private void reverseImage() {
        TransformableCreatureCard card = (TransformableCreatureCard) bean;
        execute(() -> front.setSrc(card.getBack().toString()));
        onImageClick = this::unReverseImage;
    }

    private void unReverseImage() {
        execute(() -> {
            front.setSrc(bean.getFront().toString());
            refreshActions();
        });
    }

    private void refreshActions() {
        reverse.setVisible(bean instanceof TransformableCreatureCard);
        onImageClick = this::reverseImage;
        binder.clearForm(this);
    }
}
