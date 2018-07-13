package pl.khuzzuk.mtg.organizer.extractor.rest.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetDTO {
    @JsonProperty("has_more")
    private boolean more;
    @JsonProperty("next_page")
    private URI nextPage;
    private List<CardDTO> data;
}
