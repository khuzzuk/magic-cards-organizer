package pl.khuzzuk.mtg.organizer.extractor.rest;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.client.RestTemplate;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;

import java.net.URISyntaxException;
import java.net.URL;

@RequiredArgsConstructor
public class ScryfallClient implements Loadable {
    private final Bus<Event> bus;
    private RestTemplate restTemplate;

    @Override
    public void load() {
        restTemplate = new RestTemplate();
        bus.subscribingFor(Event.CARD_FROM_URL).accept(this::downloadCard).subscribe();
    }

    @SneakyThrows(URISyntaxException.class)
    private void downloadCard(URL url) {
        CardDTO cardDTO = restTemplate.getForObject(url.toURI(), CardDTO.class);
        RulingsDTO rulingsDTO = restTemplate.getForObject(cardDTO.getRulingsUri(), RulingsDTO.class);
        cardDTO.setRulings(rulingsDTO);
        bus.message(Event.CARD_DTO_JSON).withContent(cardDTO).send();
    }
}
