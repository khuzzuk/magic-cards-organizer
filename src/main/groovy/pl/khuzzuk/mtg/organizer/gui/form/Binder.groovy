package pl.khuzzuk.mtg.organizer.gui.form

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.apache.commons.collections4.Factory
import org.apache.commons.collections4.keyvalue.MultiKey
import org.apache.commons.collections4.map.LazyMap
import org.apache.commons.lang3.StringUtils
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.initialize.Identification
import pl.khuzzuk.mtg.organizer.initialize.Loadable

import java.lang.reflect.Field
import java.util.function.BiConsumer
import java.util.function.Function

@Identification(Event.BINDER)
class Binder implements Loadable {
    private Map<Class<? extends Node>, BiConsumer<?, ?>> setters
    private Map<Class<? extends Node>, List<PropertyController>> controllers
    private Map<MultiKey<Class<?>>, ValueConverter<?, ?>> converters

    @Override
    void load() {
        setters = new HashMap<>()
        setters.put(Label.class, { Label field, String value -> field.text = value } as BiConsumer<?, String>)
        setters.put(ImageView.class, { ImageView image, Image value -> image.image = value } as BiConsumer<?, String>)

        controllers = new HashMap<>()

        Factory<ValueConverter<?, ?>> factory = { ValueConverter.DEFAULT_CONVERTER }
        converters = LazyMap.lazyMap(new HashMap<>(), factory)
        converters.put(new MultiKey(URL.class, ImageView.class), new ValueConverter<URL, Image>(
                { new Image(it.toString()) },
                { new URL(it.url) }))
        converters.put(new MultiKey(int.class, Label.class), new ValueConverter<Integer, String>(
                { it.toString() },
                { Integer.valueOf(it) }))
    }

    void bind(Class<?> beanClass, Class<? extends Node> formClass) {
        List<PropertyController> formHandlers = controllers.computeIfAbsent(formClass, { new ArrayList<>() })
        Map<String, List<Field>> hideCheckFields = getHideCheckFields(formClass)

        for (Field formField : formClass.getDeclaredFields()) {
            if (!formField.isAnnotationPresent(FormProperty)) continue

            FormProperty formProperty = formField.getDeclaredAnnotation(FormProperty)
            String path = '' == formProperty.beanPath() ? formField.name : formProperty.beanPath()
            if (!BeanReflection.hasGetterForPath(path, beanClass)) continue

            formField.setAccessible(true)

            PropertyController controller = new PropertyController()
            controller.converter = converters.get(new MultiKey<>(BeanReflection.getFieldTypeFor(path, beanClass), formField.type))
            controller.formField = formField
            controller.beanGetter = BeanReflection.propertyGetterFor(path, beanClass)
            controller.formSetter = setters.get(formField.type) as BiConsumer
            controller.hide = formProperty.hideAfterClear()
            controller.defaultValue = formProperty.defaultValue()
            controller.hideCheckFields = hideCheckFields.computeIfAbsent(formField.name, { new ArrayList<>() })
            formHandlers.add(controller)
        }
    }

    void clearForm(Node form) {
        synchronized (form) {
            controllers.get(form.class).forEach({ PropertyController controller ->
                Node formFieldElement = controller.formField.get(form) as Node

                formFieldElement.visible = !controller.hide
                controller.hideCheckFields.stream()
                        .map({it.get(form) as Node})
                        .forEach({it.visible = !controller.hide})

                def convertedValue
                if (StringUtils.isNotBlank(controller.defaultValue)) {
                    convertedValue = controller.converter.formConverter.apply(controller.defaultValue)
                } else {
                    convertedValue = controller.converter.formDefaultValue
                }
                controller.formSetter.accept(formFieldElement, convertedValue)
            })
        }
    }

    void fillForm(Node form, Object bean) {
        synchronized (form) {
            for (PropertyController controller in controllers.get(form.class)) {
                def beanFieldValue = controller.beanGetter.apply(bean)
                if (beanFieldValue != null) {
                    Node formFieldElement = controller.formField.get(form) as Node
                    def formFieldValue = controller.converter.formConverter.apply(beanFieldValue)
                    controller.formSetter.accept(formFieldElement, formFieldValue)
                    formFieldElement.visible = true
                    controller.hideCheckFields.stream().map({it.get(form) as Node}).forEach({it.visible = true})
                }
            }
        }
    }

    static Map<String, List<Field>> getHideCheckFields(Class<?> form) {
        Map<String, List<Field>> checks = new HashMap<>()
        for (Field field : form.getDeclaredFields()) {
            if (!field.isAnnotationPresent(HideCheck)) continue

            field.setAccessible(true)
            HideCheck check = field.getAnnotation(HideCheck)
            checks.computeIfAbsent(check.value(), {new ArrayList<>()}).add(field)
        }
        checks
    }

    private class PropertyController {
        Field formField
        Function<?, ?> beanGetter
        BiConsumer formSetter
        ValueConverter<?, ?> converter
        boolean hide
        String defaultValue
        List<Field> hideCheckFields
    }
}
