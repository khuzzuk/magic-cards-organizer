package pl.khuzzuk.mtg.organizer.events;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.khuzzuk.messaging.Bus;

@Configuration
public class EventsConfig {
    @Bean
    Bus<Event> bus() {
        return Bus.initializeBus(Event.class, System.out, true, 3);
    }
}
