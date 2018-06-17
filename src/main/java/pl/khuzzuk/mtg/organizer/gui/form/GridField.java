package pl.khuzzuk.mtg.organizer.gui.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GridField {
    int column() default 0;
    int row() default 0;
    int columnSpan() default 1;
    int rowSpan() default 1;
}
