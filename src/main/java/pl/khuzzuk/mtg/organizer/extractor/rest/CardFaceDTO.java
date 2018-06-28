package pl.khuzzuk.mtg.organizer.extractor.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardFaceDTO {
    private String name;
    @JsonProperty("mana_cost")
    private String manaCost;
    @JsonProperty("type_line")
    private String typeLine;
    @JsonProperty("oracle_text")
    private String oracleText;
    private String[] colors;
    private String power;
    private String toughness;
    @JsonProperty("flavor_text")
    private String flavorText;
    @JsonProperty("image_uris")
    private ImageUrisDTO imageUris;
}
