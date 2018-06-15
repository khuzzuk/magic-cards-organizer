package pl.khuzzuk.mtg.organizer

import javafx.application.Platform
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.testfx.framework.spock.ApplicationSpec

abstract class JavaFxSpecification extends ApplicationSpec {
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
            javafx.event.Event.fireEvent(node, new KeyEvent(KeyEvent.KEY_RELEASED, '', '', keyCode,
                    shift, control, alt, false))
        })
    }
}
