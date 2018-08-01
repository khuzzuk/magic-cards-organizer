package pl.khuzzuk.binder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HideCheck {
    /**
     * check other field in form, and if it has default value, this one will be hidden
     *
     * @return
     */
    String value();
}
