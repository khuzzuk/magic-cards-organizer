package pl.khuzzuk.mtg.organizer.gui.form;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import lombok.experimental.UtilityClass;
import pl.khuzzuk.mtg.organizer.common.ReflectionUtils;

import java.util.Arrays;

@UtilityClass
public class GridLayoutController {
    public static void placeFieldOnGrid(final Object form, final GridPane grid) {
        Arrays.stream(form.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(GridField.class))
                .peek(field -> field.setAccessible(true))
                .forEach(field -> {
                    Node fieldValue = ReflectionUtils.getValueFromField(field, form, e -> {
                        e.printStackTrace();
                        return null;
                    });
                    GridField pos = field.getAnnotation(GridField.class);
                    grid.add(fieldValue, pos.column(), pos.row(), pos.columnSpan(), pos.rowSpan());
                });
    }
}
