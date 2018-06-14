package pl.khuzzuk.mtg.organizer.gui.form

import javafx.scene.Node
import javafx.scene.layout.GridPane

class GridLayoutController {
    static void placeFieldOnGrid(Object form, GridPane grid) {
        Arrays.stream(form.class.getDeclaredFields())
                .filter({it.isAnnotationPresent(GridField)})
                .peek({it.setAccessible(true)})
                .forEach({
            Node field = it.get(form) as Node
            GridField pos = it.getAnnotation(GridField)
            grid.add(field, pos.column(), pos.row(), pos.columnSpan(), pos.rowSpan())
        })
    }
}
