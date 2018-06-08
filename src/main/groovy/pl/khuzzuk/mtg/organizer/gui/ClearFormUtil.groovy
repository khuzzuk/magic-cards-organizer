package pl.khuzzuk.mtg.organizer.gui

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.ImageView

import java.lang.reflect.Field
import java.util.function.BiConsumer

class ClearFormUtil {
    private static final Map<Class<? extends Node>, BiConsumer<?, String>> SETTERS = new HashMap<>()
    static {
        SETTERS.put(Label.class, {Label field, String value -> field.text = value} as BiConsumer<?, String>)
        SETTERS.put(ImageView.class, { ImageView image, value -> image.image = null } as BiConsumer<?, String>)
    }

    static void clear(Object node) {
        for (Field field : node.class.getDeclaredFields()) {
            Class<?> type = field.getType()
            if (field.isAnnotationPresent(Clearable) && SETTERS.containsKey(type)) {
                field.setAccessible(true)
                BiConsumer<?, String> setter = SETTERS.get(type) as BiConsumer<?, String>
                setter.accept(
                        field.get(node),
                        field.getDeclaredAnnotation(Clearable).value()
                )
            }
        }
    }
}
