package pl.khuzzuk.mtg.organizer

import javafx.application.Platform
import spock.lang.Specification

class JavaFxSpecification extends Specification {
    void setup() {
        Platform.startup({})
    }
}
