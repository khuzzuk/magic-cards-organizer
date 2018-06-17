package pl.khuzzuk.mtg.organizer

import javafx.application.Platform
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import spock.lang.Specification

abstract class JavaFxSpecification extends Specification {
    void setupSpec() {
        Platform.startup({})
    }

    void clickMouseOn(javafx.scene.Node node, int x, int y) {
        javafx.event.Event.fireEvent(node, new MouseEvent(
                MouseEvent.MOUSE_CLICKED, x, y, x, y, MouseButton.PRIMARY, 1,
                false, false, false, false, false,
                false, false, false,
                false, false, null))
    }

    void fireEventOn(javafx.scene.Node node, KeyCode keyCode, boolean shift, boolean control, boolean alt) {
        Platform.runLater({
            javafx.event.Event.fireEvent(node, new KeyEvent(KeyEvent.KEY_PRESSED, '', '', keyCode,
                    shift, control, alt, false))
        })
    }
}
