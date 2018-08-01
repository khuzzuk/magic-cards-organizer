package pl.khuzzuk.binder;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

public class JavaFxBinder {
    @SuppressWarnings("unchecked")
    static Binder forJavaFx() {
        Binder binder = new Binder();
        binder.addHandling(String.class, Label.class, ValueConverter.DEFAULT_CONVERTER, Labeled::setText, Label::setVisible);
        binder.addHandling(Integer.class, Label.class, ValueConverter.create(Object::toString, Integer::valueOf, "0"),
                Labeled::setText, Node::setVisible);
        binder.addHandling(int.class, Label.class, ValueConverter.create(Object::toString, Integer::valueOf, "0"),
                Labeled::setText, Node::setVisible);

        ValueConverter<URL, Image> urlToImageConverter = ValueConverter.create(
                url -> new Image(url.toString()),
                image -> UrlUtil.getOrNull(image.getUrl()),
                Image::new, ValueConverter.EMPTY_SUPPLIER);
        binder.addHandling(URL.class, ImageView.class, urlToImageConverter, ImageView::setImage, Node::setVisible);

        return binder;
    }
}
