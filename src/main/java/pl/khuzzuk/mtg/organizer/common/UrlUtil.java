package pl.khuzzuk.mtg.organizer.common;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

@UtilityClass
public class UrlUtil {
    public static Optional<URL> getUrlWithHandler(String url, Function<MalformedURLException, Optional<URL>> exceptionConsumer) {
        try {
            return Optional.of(new URL(url));
        } catch (MalformedURLException e) {
            return exceptionConsumer.apply(e);
        }
    }

    public static Optional<URI> getUriWithHandler(String uri, Function<URISyntaxException, Optional<URI>> exceptionConsumer) {
        try {
            return Optional.of(new URI(uri));
        } catch (URISyntaxException e) {
            return exceptionConsumer.apply(e);
        }
    }

    @SneakyThrows(MalformedURLException.class)
    public static URL getUrlOrNull(String url) {
        return new URL(url);
    }
}
