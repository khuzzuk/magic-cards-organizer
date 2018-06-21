package pl.khuzzuk.mtg.organizer.extractor.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageUrisDTO {
    URI small;
    URI normal;
    URI large;
    URI png;
    @JsonProperty("art_crop")
    URI artCrop;
    @JsonProperty("border_crop")
    URI borderCrop;
}
