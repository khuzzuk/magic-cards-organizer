package pl.khuzzuk.mtg.organizer.serialization;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

class ImageDownloader {
    URL downloadImage(URL url, Path path) throws DownloadException {
        try (InputStream urlStream = new BufferedInputStream(url.openStream())) {
            Files.copy(urlStream, path);
            return path.toUri().toURL();
        } catch (IOException e) {
            throw new DownloadException(e);
        }
    }

    public static class DownloadException extends Exception {
        public DownloadException(Throwable cause) {
            super(cause);
        }
    }
}
