package pl.khuzzuk.mtg.organizer.gui;

import javafx.scene.control.TextField;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class MainWindowProperties {
    public static TextField mainWindowUrl;

    @After("set(javafx.scene.control.TextField pl.khuzzuk.mtg.organizer.gui.MainWindow.url) && args(urlField)")
    public void retrieveMainWindowScene(TextField urlField) {
        mainWindowUrl = urlField;
    }
}
