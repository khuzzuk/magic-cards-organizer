package pl.khuzzuk.mtg.organizer.extractor.rest;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import lombok.SneakyThrows;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import pl.khuzzuk.mtg.organizer.model.card.Card;

@MapperConfig
public interface CardMapperConfig {
   @Mapping(source = "imageUris.png", target = "front")
   @Mapping(source = "imageUris.artCrop", target = "art")
   @Mapping(source = "uri", target = "source")
   @Mapping(source = "set", target = "printRef")
   @Mapping(source = "setName", target = "printFullName")
   @Mapping(source = "collectorNumber", target = "printOrder")
   Card toCard(CardDTO cardDTO);

   @SneakyThrows(MalformedURLException.class)
   default URL urlFrom(URI uri) {
      return uri.toURL();
   }
}
