package pl.khuzzuk.mtg.organizer.model.type;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pl.khuzzuk.mtg.organizer.model.ManaType;

@Getter
@Setter
public class Type {
    private BasicType basicType;
    private List<ManaType> colors = new ArrayList<>();
    private List<String> primaryTypes = new ArrayList<>();
    private List<String> secondaryTypes = new ArrayList<>();
}
