package pl.khuzzuk.mtg.organizer.common;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.function.Function;

@UtilityClass
public class ReflectionUtils {
    @SuppressWarnings("unchecked")
    public <T> T getValueFromField(Field field, Object owner, Function<IllegalAccessException, T> handler) {
        try {
            return (T) field.get(owner);
        } catch (IllegalAccessException e) {
            return handler.apply(e);
        }
    }
}
