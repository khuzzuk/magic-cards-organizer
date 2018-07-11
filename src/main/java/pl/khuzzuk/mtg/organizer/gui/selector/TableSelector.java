package pl.khuzzuk.mtg.organizer.gui.selector;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import lombok.RequiredArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.initialize.Identification;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;
import pl.khuzzuk.mtg.organizer.model.CardQuery;
import pl.khuzzuk.mtg.organizer.model.card.Card;

import java.util.Collection;
import java.util.function.Function;

import static pl.khuzzuk.mtg.organizer.events.Event.*;

@RequiredArgsConstructor
@Identification(TABLE_SELECTOR)
public class TableSelector extends TableView<Card> implements Loadable {
    private final Bus<Event> bus;
    private CardQuery cardQuery;

    @Override
    @SuppressWarnings("unchecked")
    public void load() {
        cardQuery = new CardQuery();

        getColumns().addAll(
                createColumn("name", Card::getName, 400),
                createColumn("rarity", card -> card.getRarity().name().toLowerCase(), 100),
                createColumn("type", card -> card.getType().getBasicType().name().toLowerCase(), 100)
        );

        bus.subscribingFor(CARD_LIST_SHOW).onFXThread().accept(this::refreshTable).subscribe();
        setOnMouseClicked(event -> requestCardView());
    }

    private void refreshTable(Collection<Card> cards) {
        getItems().clear();
        getItems().addAll(cards);
    }

    private TableColumn<Card, String> createColumn(String name, Function<Card, String> getter, int width) {
        TableColumn<Card, String> column = new TableColumn<>(name);
        column.setCellValueFactory(param -> new SimpleObjectProperty<>(getter.apply(param.getValue())));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setPrefWidth(width);
        return column;
    }

    private void requestCardView() {
        Card card = getSelectionModel().getSelectedItem();
        if (card != null) {
            bus.message(CARD_SHOW).withContent(card).send();
        }
    }
}
