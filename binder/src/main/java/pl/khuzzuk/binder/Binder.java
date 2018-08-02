package pl.khuzzuk.binder;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Binder {
    private Map<Class<?>, BiConsumer<?, ?>> setters = new HashMap<>();
    private Map<Class<?>, BiConsumer> visibilitySetters = new HashMap<>();
    private Map<BindId, List<PropertyController>> controllers = new HashMap<>();
    private Map<BindId, ValueConverter<?, ?>> converters = new HashMap<>();

    public <V, U, E> void addHandling(
            Class<V> valueType,
            Class<E> elementType,
            ValueConverter<V, U> valueConverter,
            BiConsumer<E, U> elementSetter,
            BiConsumer<E, Boolean> visibilitySetter) {
        BindId bindId = new BindId(valueType, elementType);
        converters.put(bindId, valueConverter);
        setters.put(elementType, elementSetter);
        visibilitySetters.put(elementType, visibilitySetter);
    }

    public <U> void addVisibilitySetter(Class<U> type, BiConsumer<U, Boolean> visibilitySetter) {
        visibilitySetters.put(type, visibilitySetter);
    }

    @SuppressWarnings("unchecked") //Should be checked on addHandling generics
    public void bind(Class<?> beanClass, Class<?> formClass) {
        List<PropertyController> formHandlers = controllers
                .computeIfAbsent(new BindId(formClass, beanClass), k -> new ArrayList<>());
        Map<String, List<Field>> hideCheckFields = getHideCheckFields(formClass);

        for (Field formField : formClass.getDeclaredFields()) {
            if (!formField.isAnnotationPresent(FormProperty.class)) continue;

            FormProperty formProperty = formField.getDeclaredAnnotation(FormProperty.class);
            String path = "".equals(formProperty.beanPath()) ? formField.getName() : formProperty.beanPath();
            if (!BeanReflection.hasGetterForPath(path, beanClass)) continue;

            formField.setAccessible(true);

            PropertyController controller = new PropertyController();
            ValueConverter<?, ?> converter = converters.get(new BindId(BeanReflection.getFieldTypeFor(path, beanClass), formField.getType()));
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

    @SuppressWarnings("unchecked") //Should be checked on addHandling generics
    public void clearForm(Object form) {
        controllers.keySet().stream().filter(key -> key.getLeft().equals(form.getClass()))
                .map(controllers::get)
                .flatMap(List::stream)
                .map(PropertyController.class::cast)
                .forEach(propertyController -> clearField(propertyController, form));
    }

    @SuppressWarnings("unchecked") //Should be checked on addHandling generics
    private void clearField(PropertyController controller, Object form) {
        Object element = BeanReflection.getValueFromField(controller.formField, form, this::rethrow);
        if (element != null) {
            setVisible(element, controller);
            controller.getHideCheckFields().forEach(field -> hideBoundedSiblingField(field, form, controller));

            Object convertedValue = controller.getConverter().fromDefaultValue(controller.getDefaultValue());
            controller.getFormSetter().accept(element, convertedValue);
        }
    }

    private void hideBoundedSiblingField(Field field, Object form, PropertyController controller) {
        Object element = BeanReflection.getValueFromField(field, form, this::rethrow);
        if (element != null) {
            setVisible(element, controller);
        }
    }

    @SuppressWarnings("unchecked") //Should be checked on addHandling generics
    public void fillForm(Object form, Object bean) {
        for (PropertyController controller : controllers.get(new BindId(form.getClass(), bean.getClass()))) {
            Object beanFieldValue = controller.getBeanGetter().apply(bean);
            if (beanFieldValue != null) {
                Object formFieldElement = BeanReflection.getValueFromField(controller.formField, form, this::rethrow);
                Object formFieldValue = controller.getConverter().getFormConverter().apply(beanFieldValue);
                controller.getFormSetter().accept(formFieldElement, formFieldValue);
                visibilitySetters.get(formFieldElement.getClass()).accept(formFieldElement, true);
                controller.getHideCheckFields().stream()
                        .map(hiddenField -> BeanReflection.getValueFromField(hiddenField, form, this::rethrow))
                        .forEach(t -> setVisible(t, controller));
            }
        }
    }

    @SuppressWarnings("unchecked") //Should be checked on addHandling generics
    private void setVisible(Object element, PropertyController controller) {
        visibilitySetters.get(element.getClass()).accept(element, !controller.isHide());
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

    private <T> T rethrow(Exception e) {
        throw new BinderException(e);
    }

    private static class BinderException extends RuntimeException {
        private BinderException(Throwable cause) {
            super(cause);
        }
    }
}
