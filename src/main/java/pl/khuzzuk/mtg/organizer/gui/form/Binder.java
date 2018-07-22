package pl.khuzzuk.mtg.organizer.gui.form;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pl.khuzzuk.mtg.organizer.common.ReflectionUtils;
import pl.khuzzuk.mtg.organizer.common.UrlUtil;
import pl.khuzzuk.mtg.organizer.initialize.Loadable;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Component
public class Binder implements Loadable {
    private Map<Class<? extends Node>, BiConsumer<?, ?>> setters;
    private Map<MultiKey<Class<?>>, List<PropertyController>> controllers;
    private Map<MultiKey<Class<?>>, ValueConverter<?, ?, String>> converters;

    @Override
    @SuppressWarnings("unchecked")
    public void load() {
        setters = new HashMap<>();
        setters.put(Label.class, (BiConsumer<Label, String>) Labeled::setText);
        setters.put(ImageView.class, (BiConsumer<ImageView, Image>) ImageView::setImage);

        controllers = new HashMap<>();
        converters = new HashMap();
        converters.put(new MultiKey(String.class, Label.class), ValueConverter.DEFAULT_CONVERTER);
        converters.put(new MultiKey(Integer.class, Label.class), new ValueConverter<>(Object::toString, Integer::valueOf, "0"));
        converters.put(new MultiKey(int.class, Label.class), new ValueConverter<>(Object::toString, Integer::valueOf, "0"));
        converters.put(new MultiKey(URL.class, ImageView.class), new ValueConverter<>(
                url -> new Image(url.toString()),
                image -> UrlUtil.getUrlWithHandler(image.getUrl(), e -> {
                    e.printStackTrace();
                    return null;
                })));
    }

    @SuppressWarnings("unchecked")
    public void bind(Class<?> beanClass, Class<?> formClass) {
        List<PropertyController> formHandlers = controllers
                .computeIfAbsent(new MultiKey<>(formClass, beanClass), k -> new ArrayList<>());
        Map<String, List<Field>> hideCheckFields = getHideCheckFields(formClass);

        for (Field formField : formClass.getDeclaredFields()) {
            if (!formField.isAnnotationPresent(FormProperty.class)) continue;

            FormProperty formProperty = formField.getDeclaredAnnotation(FormProperty.class);
            String path = "".equals(formProperty.beanPath()) ? formField.getName() : formProperty.beanPath();
            if (!BeanReflection.hasGetterForPath(path, beanClass)) continue;

            formField.setAccessible(true);

            PropertyController controller = new PropertyController();
            ValueConverter<?, ?, String> converter = converters.get(new MultiKey(BeanReflection.getFieldTypeFor(path, beanClass), formField.getType()));
            controller.setConverter(converter);
            controller.setFormField(formField);
            controller.setBeanGetter(BeanReflection.propertyGetterFor(path, beanClass));
            controller.setFormSetter(setters.get(formField.getType()));
            controller.setHide(formProperty.hideAfterClear());
            controller.setDefaultValue(formProperty.defaultValue());
            controller.setHideCheckFields(hideCheckFields.computeIfAbsent(formField.getName(), k -> new ArrayList<>()));
            formHandlers.add(controller);
        }
    }

    @SuppressWarnings("unchecked")
    public void clearForm(final Node form) {
        controllers.keySet().stream().filter(key -> key.getKey(0).equals(form.getClass()))
                .map(controllers::get)
                .flatMap(List::stream)
                .map(PropertyController.class::cast)
                .forEach(controller -> {
                    Node formFieldElement = ReflectionUtils.getValueFromField(controller.formField, form, this::rethrow);
                    if (formFieldElement != null) {
                        formFieldElement.setVisible(!controller.isHide());
                        controller.getHideCheckFields().stream()
                                .map(field -> mapFieldToNode(field, form))
                                .filter(Objects::nonNull)
                                .forEach(toCheck -> setVisible(toCheck, !controller.isHide()));

                        Object convertedValue;
                        if (StringUtils.isNotBlank(controller.getDefaultValue())) {
                            convertedValue = controller.getConverter().getFormConverter().apply(controller.getDefaultValue());
                        } else {
                            convertedValue = controller.getConverter().getFormDefaultValue();
                        }
                        controller.getFormSetter().accept(formFieldElement, convertedValue);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public void fillForm(final Node form, Object bean) {
        for (PropertyController controller : controllers.get(new MultiKey<>(form.getClass(), bean.getClass()))) {
            Object beanFieldValue = controller.getBeanGetter().apply(bean);
            if (beanFieldValue != null) {
                Node formFieldElement = ReflectionUtils.getValueFromField(controller.formField, form, this::rethrow);
                Object formFieldValue = controller.getConverter().getFormConverter().apply(beanFieldValue);
                controller.getFormSetter().accept(formFieldElement, formFieldValue);
                formFieldElement.setVisible(true);
                controller.getHideCheckFields().stream()
                        .map(hiddenField -> extract(hiddenField, form))
                        .forEach(node -> node.setVisible(true));
            }
        }
    }

    private static Map<String, List<Field>> getHideCheckFields(Class<?> form) {
        Map<String, List<Field>> checks = new HashMap<>();
        for (Field field : form.getDeclaredFields()) {
            if (!field.isAnnotationPresent(HideCheck.class)) continue;

            field.setAccessible(true);
            HideCheck check = field.getAnnotation(HideCheck.class);
            checks.computeIfAbsent(check.value(), k -> new ArrayList<>()).add(field);
        }

        return checks;
    }

    @Getter
    @Setter
    private class PropertyController {
        private Field formField;
        private Function beanGetter;
        private BiConsumer formSetter;
        private ValueConverter converter;
        private boolean hide;
        private String defaultValue;
        private List<Field> hideCheckFields;
    }

    private static boolean setVisible(Node propOwner, boolean var1) {
        propOwner.setVisible(var1);
        return var1;
    }

    private static Object extract(Field field, Object owner) {
        try {
            return field.get(owner);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> T rethrow(Exception e) {
        throw new BinderException(e);
    }

    private class BinderException extends RuntimeException {
        private BinderException(Throwable cause) {
            super(cause);
        }
    }
}
