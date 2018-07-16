package pl.khuzzuk.mtg.organizer.web.initialize;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HtmlContainer;
import org.apache.commons.lang3.StringUtils;
import org.jooq.lambda.Seq;
import org.jooq.lambda.Unchecked;
import pl.khuzzuk.mtg.organizer.web.CSS;

public class ComponentInitialization {
    public static void initializeComponents(HasComponents container) {
        if (container instanceof HtmlContainer) {
            componentInitiaclization((HtmlContainer) container);
        }

        Seq.of(container.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(UIProperty.class))
                .peek(f -> f.setAccessible(true))
                .map(Unchecked.function(f -> f.get(container)))
                .ofType(Component.class)
                .reverse()
                .forEach(container::add);
    }

    private static void componentInitiaclization(HtmlContainer component) {
        if (component.getClass().isAnnotationPresent(CSS.class)) {
            CSS css = component.getClass().getDeclaredAnnotation(CSS.class);
            if (StringUtils.isNotBlank(css.id())) {
                component.setId(css.id());
            }
            if (StringUtils.isNotBlank(css.className())) {
                component.setClassName(css.className());
            }
        }
    }
}
