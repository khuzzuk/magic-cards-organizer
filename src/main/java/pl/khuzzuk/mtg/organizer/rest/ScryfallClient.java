package pl.khuzzuk.mtg.organizer.rest;

import java.net.URISyntaxException;
import java.net.URL;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.client.RestTemplate;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;

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
      bus.message(Event.CARD_DTO_JSON).withContent(cardDTO).send();
   }
}
