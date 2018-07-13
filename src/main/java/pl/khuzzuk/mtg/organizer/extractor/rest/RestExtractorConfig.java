package pl.khuzzuk.mtg.organizer.extractor.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
class RestExtractorConfig {
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
