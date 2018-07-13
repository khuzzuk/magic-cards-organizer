package pl.khuzzuk.mtg.organizer.serialization;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static java.lang.String.format;
import static pl.khuzzuk.mtg.organizer.events.Event.DOWNLOAD_IMAGE;
import static pl.khuzzuk.mtg.organizer.events.Event.ERROR;

@RequiredArgsConstructor
@Component
class ImageDownloader implements InitializingBean {
    private final Bus<Event> bus;

    @Override
    public void afterPropertiesSet() {
        bus.subscribingFor(DOWNLOAD_IMAGE).accept(this::downloadImage).subscribe();
    }

    void downloadImage(Pair<URL, Path> urls) {
        try (InputStream urlStream = new BufferedInputStream(urls.getLeft().openStream())) {
            Files.copy(urlStream, urls.getRight(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            bus.message(ERROR).withContent(format("Download image failed: %s", e)).send();
        }
    }
}
