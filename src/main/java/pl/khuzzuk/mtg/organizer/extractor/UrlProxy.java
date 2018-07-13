package pl.khuzzuk.mtg.organizer.extractor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;

import java.net.URL;

import static pl.khuzzuk.mtg.organizer.events.Event.*;

@RequiredArgsConstructor
@Component
public class UrlProxy implements InitializingBean {
    private final Bus<Event> bus;

    @Override
    public void afterPropertiesSet() {
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
