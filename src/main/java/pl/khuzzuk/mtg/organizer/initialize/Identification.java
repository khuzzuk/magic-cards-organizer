package pl.khuzzuk.mtg.organizer.initialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pl.khuzzuk.mtg.organizer.Event;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Identification{
    Event value();
}
