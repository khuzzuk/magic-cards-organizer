package pl.khuzzuk.mtg.organizer.gui.form

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface FormProperty {
    String beanPath() default ''
    String defaultValue() default ''
    boolean clear() default true
    boolean hideAfterClear() default false
}
