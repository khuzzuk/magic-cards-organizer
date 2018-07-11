package pl.khuzzuk.mtg.organizer.web;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.khuzzuk.mtg.organizer.web.bottom.StatusBar;

@Route("")
@Component
@UIScope
public class HomeView extends Div {
    @Autowired
    public HomeView(StatusBar statusBar) {
        add(statusBar);
    }
}
