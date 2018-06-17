package pl.khuzzuk.mtg.organizer.gui.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormProperty {
    String beanPath() default "";
    boolean hideAfterClear() default false;
    /** it has to be string value in converter */
    String defaultValue() default "";
}
