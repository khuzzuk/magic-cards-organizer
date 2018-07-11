package pl.khuzzuk.mtg.organizer.web.initialize;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import org.jooq.lambda.Seq;
import org.jooq.lambda.Unchecked;

public class ComponentInitialization {
    public static void initializeComponents(HasComponents container) {
        Seq.of(container.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(UIProperty.class))
                .peek(f -> f.setAccessible(true))
                .map(Unchecked.function(f -> f.get(container)))
                .ofType(Component.class)
                .reverse()
                .forEach(container::add);
    }
}
