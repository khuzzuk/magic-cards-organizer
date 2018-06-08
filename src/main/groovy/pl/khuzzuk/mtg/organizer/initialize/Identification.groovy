package pl.khuzzuk.mtg.organizer.initialize

import pl.khuzzuk.mtg.organizer.Event

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Identification {
    Event value();
}