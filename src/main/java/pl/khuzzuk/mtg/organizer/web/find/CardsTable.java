package pl.khuzzuk.mtg.organizer.web.find;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.model.card.Card;
import pl.khuzzuk.mtg.organizer.web.CardTypeNameViewService;
import pl.khuzzuk.mtg.organizer.web.WebComponent;
import pl.khuzzuk.mtg.organizer.web.initialize.CSS;
import pl.khuzzuk.mtg.organizer.web.initialize.ComponentInitialization;
import pl.khuzzuk.mtg.organizer.web.initialize.UIProperty;

import java.util.Collection;

@Component
@UIScope
@RequiredArgsConstructor
@Tag("CardsTable")
public class CardsTable extends WebComponent implements InitializingBean {
    private final Bus<Event> bus;
    private final CardTypeNameViewService cardTypeNameViewService;

    @UIProperty
    @CSS(className = "cards-table-content")
    private Grid<Card> table = new Grid<>();

    @Override
    public void afterPropertiesSet() {
        prepareView();
        bus.subscribingFor(Event.CARD_LIST_SHOW).accept(this::listCards).subscribe();
    }

    private void prepareView() {
        ComponentInitialization.initializeComponents(this);
        table.addColumn(Card::getName).setHeader("name");
        table.addColumn(card -> card.getRarity().name().toLowerCase()).setHeader("rarity");
        table.addColumn(cardTypeNameViewService::getNameByType).setHeader("basic type");
        table.addColumn(Card::getType).setHeader("type");
        table.addSelectionListener(this::onSelection);
    }

    private void listCards(Collection<Card> cards) {
        execute(() -> table.setItems(cards));
    }

    private void onSelection(SelectionEvent<Grid<Card>, Card> event) {
        event.getFirstSelectedItem().ifPresent(card -> bus.message(Event.CARD_DATA).withContent(card).send());
    }
}
