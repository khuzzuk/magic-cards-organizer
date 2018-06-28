package pl.khuzzuk.mtg.organizer.gui.filter;

import javafx.scene.control.ListView;
import lombok.RequiredArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.initialize.Identification;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;
import pl.khuzzuk.mtg.organizer.model.CardQuery;

import java.util.SortedSet;

import static pl.khuzzuk.mtg.organizer.Event.*;

@RequiredArgsConstructor
@Identification(LEFT_PANE_FILTER)
public class LeftPaneFilter extends ListView<String> implements Loadable {
    private final Bus<Event> bus;
    private CardQuery cardQuery;

    @Override
    public void load() {
        cardQuery = new CardQuery();
        bus.subscribingFor(WINDOW_TO_SHOW).then(this::initialize).subscribe();
    }

    private void initialize() {
        bus.subscribingFor(REFRESH_CARD_SETS).onFXThread().accept(this::refreshSets).subscribe();
        bus.subscribingFor(CARD_DATA).then(this::requestSetsRefresh).subscribe();
        requestSetsRefresh();
        setOnMouseClicked(event -> listSetCards());
    }

    private void requestSetsRefresh() {
        bus.message(CARD_SETS).withResponse(REFRESH_CARD_SETS).send();
    }

    private void refreshSets(SortedSet<String> sets) {
        getItems().clear();
        getItems().addAll(sets);
    }

    private void listSetCards() {
        bus.message(CARD_FIND)
                .withContent(cardQuery.withSetName(getSelectionModel().getSelectedItem()))
                .withResponse(CARD_LIST_SHOW)
                .send();
    }
}
