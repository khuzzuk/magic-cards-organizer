package pl.khuzzuk.mtg.organizer.initialize

import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.Event

class Container {
    Collection<Loadable> loadables
    private Bus<Event> bus

    Container(Bus<Event> bus, Collection<Loadable> loadables = new ArrayList<>()) {
        this.loadables = loadables
        this.bus = bus
    }

    void prepare(Loadable loadable) {
        loadables.add(loadable)
        loadable.load()
    }

    void sealContainer() {
        loadables.forEach({send(it)})
    }

    private void send(Loadable loadable) {
        if (loadable.class.isAnnotationPresent(Identification.class)) {
            bus.message(loadable.class.getDeclaredAnnotation(Identification.class)?.value())
                    .withContent(loadable)
                    .send()
        }
    }
}
