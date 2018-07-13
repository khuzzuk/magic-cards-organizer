package pl.khuzzuk.mtg.organizer.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.khuzzuk.messaging.Bus;
import pl.khuzzuk.mtg.organizer.events.Event;
import pl.khuzzuk.mtg.organizer.model.skill.PredefinedSkill;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PredefinedSkillRepo implements InitializingBean {
    private final Bus<Event> bus;
    private Map<String, PredefinedSkill> predefinedSkills;

    @Override
    public void afterPropertiesSet() {
        try {
            Path jsonFilePath = Paths.get(getClass().getClassLoader().getResource("predefinedSkills.json").toURI());
            String json = Files.readAllLines(jsonFilePath).stream().collect(Collectors.joining());
            ObjectMapper mapper = new ObjectMapper();
            PredefinedSkillContainer predefinedSkillContainer = mapper.readValue(json, PredefinedSkillContainer.class);
            predefinedSkills = predefinedSkillContainer.predefinedSkills.stream()
                    .collect(Collectors.toMap(skill -> skill.getText().toLowerCase(), Function.identity()));
        } catch (IOException | URISyntaxException e) {
            bus.message(Event.ERROR).withContent("Predefined skills read error").send();
        }
    }

    public boolean exists(String name) {
        return predefinedSkills.containsKey(name.toLowerCase());
    }

    public List<PredefinedSkill> findAllBy(List<String> names) {
        return names.stream()
                .filter(this::exists)
                .map(String::toLowerCase)
                .map(predefinedSkills::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    static class PredefinedSkillContainer {
        private List<PredefinedSkill> predefinedSkills;
    }
}
