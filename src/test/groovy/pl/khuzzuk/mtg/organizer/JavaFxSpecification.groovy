package pl.khuzzuk.mtg.organizer

import javafx.application.Platform
import spock.lang.Specification

abstract class JavaFxSpecification extends Specification {
    void setupSpec() {
        Platform.startup({})
    }
}
