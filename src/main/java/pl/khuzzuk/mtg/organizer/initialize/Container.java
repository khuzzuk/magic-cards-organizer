package pl.khuzzuk.mtg.organizer.initialize;

import java.util.ArrayList;
import java.util.Collection;

import lombok.RequiredArgsConstructor;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.Event;

@RequiredArgsConstructor
public class Container
{
    private final Bus<Event> bus;
    private Collection<Loadable> toLoad = new ArrayList<>();

    public void prepare(Loadable loadable) {
        toLoad.add(loadable);
        loadable.load();
    }

    public void sealContainer() {
        toLoad.forEach(this::send);
    }

    private void send(Loadable loadable) {
        if (loadable.getClass().isAnnotationPresent(Identification.class)) {
            bus.message(loadable.getClass().getDeclaredAnnotation(Identification.class).value()).withContent(loadable)
                    .send();
        }
    }
}
