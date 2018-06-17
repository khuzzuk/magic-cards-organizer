package pl.khuzzuk.mtg.organizer.gui.card;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

import java.util.concurrent.atomic.AtomicBoolean;

@Aspect
public class CardViewerProperties {
    public static AtomicBoolean bindingFinished = new AtomicBoolean();
    public static Label name;
    public static Label text;
    public static ImageView front;

    public static void resetBindingNotification() {
        bindingFinished.set(false);
    }

    public static boolean isBindingFinished() {
        return bindingFinished.get();
    }

    @After("execution(private void pl.khuzzuk.mtg.organizer.gui.card.CardViewer.loadCard(..))")
    public void reportBindingFinish() {
        bindingFinished.set(true);
    }

    @After("set(javafx.scene.control.Label pl.khuzzuk.mtg.organizer.gui.card.CardViewer.name) && args(name)")
    public void setName(Label name) {
        CardViewerProperties.name = name;
    }

    @After("set(javafx.scene.control.Label pl.khuzzuk.mtg.organizer.gui.card.CardViewer.text) && args(text)")
    public void setText(Label text) {
        CardViewerProperties.text = text;
    }

    @After("set(javafx.scene.image.ImageView pl.khuzzuk.mtg.organizer.gui.card.CardViewer.front) && args(front)")
    public void setFront(ImageView front) {
        CardViewerProperties.front = front;
    }
}
