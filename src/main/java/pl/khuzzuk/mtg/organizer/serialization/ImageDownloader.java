package pl.khuzzuk.mtg.organizer.serialization;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

class ImageDownloader {
    URL downloadImage(URL url, Path path) throws DownloadException {
        try (InputStream urlStream = new BufferedInputStream(url.openStream())) {
            Files.copy(urlStream, path, StandardCopyOption.REPLACE_EXISTING);
            return path.toUri().toURL();
        } catch (IOException e) {
            String message;
            if (e instanceof FileAlreadyExistsException) {
                message = String.format("File already exists: %s", path);
            } else {
                message = String.format("Cannot access file: %s", path);
            }
            throw new DownloadException(message, e);
        }
    }

    static class DownloadException extends Exception {
        DownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
