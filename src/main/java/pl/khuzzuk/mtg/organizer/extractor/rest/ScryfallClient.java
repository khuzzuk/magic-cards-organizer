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
        bus.subscribingFor(Event.SET_FROM_URL).accept(this::downloadSet).subscribe();
    }

    @SneakyThrows(URISyntaxException.class)
    private void downloadCard(URL url) {
        CardDTO cardDTO = restTemplate.getForObject(url.toURI(), CardDTO.class);
        sendCard(cardDTO);
    }

    @SneakyThrows(URISyntaxException.class)
    private void downloadSet(URL url) {
        SetDTO setPage = restTemplate.getForObject(url.toURI(), SetDTO.class);
        setPage.getData().forEach(this::sendCard);

        while (setPage.isMore()) {
            setPage = restTemplate.getForObject(setPage.getNextPage(), SetDTO.class);
            setPage.getData().forEach(this::sendCard);
        }
    }

    private void sendCard(CardDTO cardDTO) {
        RulingsDTO rulingsDTO = restTemplate.getForObject(cardDTO.getRulingsUri(), RulingsDTO.class);
        cardDTO.setRulings(rulingsDTO);
        bus.message(Event.CARD_DTO_JSON).withContent(cardDTO).send();
    }
}
