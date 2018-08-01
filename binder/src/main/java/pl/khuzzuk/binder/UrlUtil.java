package pl.khuzzuk.binder;

import lombok.experimental.UtilityClass;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

@UtilityClass
class UrlUtil {
    static Optional<URL> getUrlWithHandler(String url, Function<MalformedURLException, Optional<URL>> exceptionConsumer) {
        try {
            return Optional.of(new URL(url));
        } catch (MalformedURLException e) {
            return exceptionConsumer.apply(e);
        }
    }

    static URL getOrNull(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
