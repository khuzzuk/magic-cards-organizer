package pl.khuzzuk.mtg.organizer.extractor.rest.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RulingsDTO {
    List<RuleDTO> data;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RuleDTO {
        private String source;
        @JsonProperty("published_at")
        private Date publishedAt;
        private String comment;
    }
}
