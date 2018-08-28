package pl.khuzzuk.mtg.organizer.model.type;

import lombok.Getter;
import lombok.Setter;
import org.jooq.lambda.Seq;
import pl.khuzzuk.mtg.organizer.model.ManaType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class Type {
    private BasicType basicType;
    private Set<ManaType> colors = EnumSet.noneOf(ManaType.class);
    private List<String> primaryTypes = new ArrayList<>();
    private List<String> secondaryTypes = new ArrayList<>();

    @Override
    public String toString() {
        return Seq.concat(Seq.seq(primaryTypes), Seq.seq(secondaryTypes)).collect(Collectors.joining(" "));
    }
}
