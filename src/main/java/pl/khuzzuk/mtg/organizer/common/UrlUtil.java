package pl.khuzzuk.mtg.organizer.common;

import lombok.experimental.UtilityClass;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

@UtilityClass
public class UrlUtil {
    public static URL getUrlWithHandler(String url, Function<MalformedURLException, URL> exceptionConsumer) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            return exceptionConsumer.apply(e);
        }
    }
}
