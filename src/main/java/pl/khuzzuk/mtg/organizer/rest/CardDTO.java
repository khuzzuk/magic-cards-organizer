package pl.khuzzuk.mtg.organizer.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
class CardDTO {
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
}
