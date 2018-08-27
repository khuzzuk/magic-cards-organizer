package pl.khuzzuk.mtg.organizer.web.initialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marked field content will be added to {@link com.vaadin.flow.component.HasComponents} by {@link ComponentInitialization}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UIProperty {
}
