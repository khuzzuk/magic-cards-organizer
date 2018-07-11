package pl.khuzzuk.mtg.organizer

import javafx.application.Platform
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.events.Event
import pl.khuzzuk.mtg.organizer.gui.MainWindowProperties
import pl.khuzzuk.mtg.organizer.gui.card.CardViewer
import pl.khuzzuk.mtg.organizer.gui.card.CardViewerProperties
import spock.lang.Shared

import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

import static org.awaitility.Awaitility.await

class MtgOrganizerAppSpec extends JavaFxSpecification {
    static final testFiles = Paths.get('testFiles')
    static final repoJsonfile = testFiles.resolve('testRepo.json')

    @Shared
    Bus<Event> bus
    @Shared
    PropertyContainer<CardViewer> cardViewerProperty = new PropertyContainer<>()

    void setupSpec() {
        Files.createDirectory(testFiles)
        bus = Bus.initializeBus(Event.class, System.out, true)
        interceptProperties()
        def container = MtgOrganizerApp.createContainer(bus, repoJsonfile)
        Platform.runLater({
            bus.message(Event.SET_PRIMARY_STAGE).withContent(new Stage()).send()
            bus.message(Event.FX_THREAD_STARTED).send()
            container.sealContainer()
        })
        await().atMost(3, TimeUnit.SECONDS).until({ MainWindowProperties.mainWindowUrl != null })
        await().atMost(2, TimeUnit.SECONDS).until({ cardViewerProperty.hasValue() })
    }

    void cleanupSpec() {
        Files.walk(testFiles).sorted({p1, p2 -> (p2 <=> p1) }).forEach({ Files.delete(it) })
    }

    private void interceptProperties() {
        bus.subscribingFor(Event.ERROR).accept({ println it }).subscribe()
        bus.subscribingFor(Event.CARD_VIEWER).accept({ cardViewerProperty.put(it as CardViewer) }).subscribe()
    }

    def 'check Import button for CreatureCard'() {
        given:
        MainWindowProperties.mainWindowUrl.text = 'https://api.scryfall.com/cards/soi/5?format=json&pretty=true'
        CardViewerProperties.resetBindingNotification()

        when:
        fireEventOn(MainWindowProperties.mainWindowUrl, KeyCode.ENTER, false, false, false)
        await().atMost(4, TimeUnit.SECONDS).until({ CardViewerProperties.isBindingFinished() })

        then:
        CardViewerProperties.name.text == 'Archangel Avacyn'
        CardViewerProperties.text.text == '"Wings that once bore hope are now stained with blood. She is our guardian no longer." —Grete, cathar apostate'
        CardViewerProperties.front.image.url == 'https://img.scryfall.com/cards/png/en/soi/5a.png?1518204266'
    }

    def 'check repo indexing'() {
        given:
        MainWindowProperties.mainWindowUrl.text = 'https://api.scryfall.com/cards/soi/5?format=json&pretty=true'
        CardViewerProperties.resetBindingNotification()

        when:
        fireEventOn(MainWindowProperties.mainWindowUrl, KeyCode.ENTER, false, false, false)
        await().atMost(4, TimeUnit.SECONDS).until({ CardViewerProperties.isBindingFinished() })

        then:
        Files.readAllLines(repoJsonfile).stream().anyMatch({ it.contains('"name" : "Archangel Avacyn"') })
    }
}
