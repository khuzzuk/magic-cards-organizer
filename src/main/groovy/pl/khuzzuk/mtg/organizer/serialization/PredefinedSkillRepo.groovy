package pl.khuzzuk.mtg.organizer.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.initialize.Loadable

class PredefinedSkillRepo implements Loadable {
    Bus<Event> bus

    PredefinedSkillRepo(Bus<Event> bus) {
        this.bus = bus
    }

    @Override
    void load() {

    }

    private void loadPredefinedSkills(ObjectMapper mapper) {

    }
}
