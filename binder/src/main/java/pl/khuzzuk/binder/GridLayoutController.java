package pl.khuzzuk.binder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class GridLayoutController {
    public static void placeFieldOnGrid(final Object form, final Object grid) {
        Method add = Arrays.stream(grid.getClass().getDeclaredMethods())
                .filter(m -> m.getName().equals("add") && m.getParameterCount() == 5)
                .findAny().orElseThrow(IllegalArgumentException::new);
        Arrays.stream(form.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(GridField.class))
                .peek(field -> field.setAccessible(true))
                .forEach(field -> {
                    Object fieldValue = BeanReflection.getValueFromField(field, form, e -> {
                        e.printStackTrace();
                        return null;
                    });
                    GridField pos = field.getAnnotation(GridField.class);
                    try {
                        add.invoke(grid, fieldValue, pos.column(), pos.row(), pos.columnSpan(), pos.rowSpan());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalArgumentException("Method is not accessible");
                    }
                });
    }
}
