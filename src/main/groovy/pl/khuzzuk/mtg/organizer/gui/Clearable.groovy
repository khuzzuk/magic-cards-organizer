package pl.khuzzuk.mtg.organizer.gui

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Clearable {
    String value() default ''
    boolean visibleOnClear() default true
}