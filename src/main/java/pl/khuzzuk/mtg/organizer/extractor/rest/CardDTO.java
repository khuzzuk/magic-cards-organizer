package pl.khuzzuk.mtg.organizer.extractor.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CardDTO {
    private String name;
    private URI uri;
    @JsonProperty("highres_image")
    private boolean hiResImage;
    @JsonProperty("image_uris")
    private ImageUrisDTO imageUris;
    @JsonProperty("mana_cost")
    private String manaCost;
    @JsonProperty("type_line")
    private String typeLine;
    @JsonProperty("oracle_text")
    private String oracleText;
    private String[] colors;
    @JsonProperty("color_identity")
    private String[] colorIdentity;
    private String set;
    @JsonProperty("set_name")
    private String setName;
    @JsonProperty("set_uri")
    private URI setUri;
    @JsonProperty("rulings_uri")
    private URI rulingsUri;
    @JsonProperty("collector_number")
    private int collectorNumber;
    private boolean digital;
    private String rarity;
    @JsonProperty("flavor_text")
    private String flavorText;
    private int power;
    private int toughness;
    @JsonProperty("edhrec_rank")
    private int edhrecRank;
    @JsonProperty("card_faces")
    private List<CardFaceDTO> cardFaces = new ArrayList<>();
}
