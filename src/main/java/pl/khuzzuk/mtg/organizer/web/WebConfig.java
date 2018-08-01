package pl.khuzzuk.mtg.organizer.web;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.khuzzuk.binder.Binder;
import pl.khuzzuk.binder.ValueConverter;
import pl.khuzzuk.mtg.organizer.common.UrlUtil;

import java.net.URL;
import java.util.function.Function;

@Configuration
class WebConfig {
    private static final String DEFAULT_IMAGE_PATH = WebConfig.class.getClassLoader().getResource("defaultImage.jpg").toString();

    @Bean
    Binder binder() {
        Binder binder = new Binder();

        binder.addHandling(String.class, Label.class, ValueConverter.DEFAULT_CONVERTER,
                Label::setText, Label::setVisible);

        ValueConverter<URL, String> urlConverter = ValueConverter.create(Object::toString, UrlUtil::getUrlOrNull,
                Function.identity(), () -> DEFAULT_IMAGE_PATH);
        binder.addHandling(URL.class, Image.class, urlConverter, Image::setSrc, Image::setVisible);

        binder.addVisibilitySetter(Button.class, Button::setVisible);

        return binder;
    }
}
