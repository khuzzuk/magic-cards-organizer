package pl.khuzzuk.mtg.organizer.extractor;

import lombok.RequiredArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;

import java.net.URL;

import static pl.khuzzuk.mtg.organizer.Event.CARD_FROM_URL;
import static pl.khuzzuk.mtg.organizer.Event.SET_FROM_URL;
import static pl.khuzzuk.mtg.organizer.Event.URL_TO_IMPORT;

@RequiredArgsConstructor
public class UrlProxy implements Loadable {
    private final Bus<Event> bus;

    @Override
    public void load() {
        bus.subscribingFor(URL_TO_IMPORT).accept(this::proxyUrl).subscribe();
    }

    private void proxyUrl(URL url) {
        if (url.toString().contains("search")) {
            bus.message(SET_FROM_URL).withContent(url).send();
        } else {
            bus.message(CARD_FROM_URL).withContent(url).send();
        }
    }
}
