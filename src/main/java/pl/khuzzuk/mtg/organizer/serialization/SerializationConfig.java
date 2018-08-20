package pl.khuzzuk.mtg.organizer.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

@Configuration
class SerializationConfig {
    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        objectMapper.enable(INDENT_OUTPUT);
        return objectMapper;
    }
}
