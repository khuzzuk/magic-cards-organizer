package pl.khuzzuk.mtg.organizer.web.find;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.model.CardQuery;
import pl.khuzzuk.mtg.organizer.web.WebComponent;
import pl.khuzzuk.mtg.organizer.web.initialize.ComponentInitialization;
import pl.khuzzuk.mtg.organizer.web.initialize.UIProperty;

import java.util.Set;

import static pl.khuzzuk.mtg.organizer.events.Event.*;

@RequiredArgsConstructor
@Component
@UIScope
@Tag("SetSelector")
public class SetSelector extends WebComponent implements InitializingBean {
    private final Bus<Event> bus;

    @UIProperty
    private Grid<String> setsList = new Grid<>();

    @Override
    public void afterPropertiesSet() {
        prepareView();
        bus.subscribingFor(REFRESH_CARD_SETS).accept(this::refreshSets).subscribe();
        bus.subscribingFor(CARD_DATA).then(this::requestSetRefresh).subscribe();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        requestSetRefresh();
    }

    private void prepareView() {
        ComponentInitialization.initializeComponents(this);
        setsList.addColumn(s -> s).setHeader("Sets");
        setsList.addSelectionListener(this::onSelection);
    }

    private void requestSetRefresh() {
        bus.message(Event.CARD_SETS).withResponse(Event.REFRESH_CARD_SETS).send();
    }

    private void refreshSets(Set<String> sets) {
        execute(() -> setsList.setItems(sets));
    }

    private void onSelection(SelectionEvent<Grid<String>, String> event) {
        event.getFirstSelectedItem().ifPresent(v ->
                bus.message(CARD_FIND)
                        .withResponse(CARD_LIST_SHOW)
                        .withContent(new CardQuery().withSetName(v)).send());
    }
}
