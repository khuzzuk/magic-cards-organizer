package pl.khuzzuk.mtg.organizer.model.type;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Type {
    private BasicType basicType;
    private List<String> primaryTypes = new ArrayList<>();
    private List<String> secondaryTypes = new ArrayList<>();
}
