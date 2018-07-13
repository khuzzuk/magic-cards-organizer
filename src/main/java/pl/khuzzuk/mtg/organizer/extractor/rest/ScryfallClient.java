package pl.khuzzuk.mtg.organizer.extractor.rest;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.extractor.rest.data.CardDTO;
import pl.khuzzuk.mtg.organizer.extractor.rest.data.RulingsDTO;
import pl.khuzzuk.mtg.organizer.extractor.rest.data.SetDTO;

import java.net.URISyntaxException;
import java.net.URL;

@RequiredArgsConstructor
@Component
public class ScryfallClient implements InitializingBean {
    private final Bus<Event> bus;
    private final RestTemplate restTemplate;

    @Override
    public void afterPropertiesSet() {
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
