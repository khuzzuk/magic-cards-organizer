package pl.khuzzuk.binder;

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
            Class<?> beanType = BeanReflection.getFieldTypeFor(path, beanClass);
            ValueConverter<?, ?> converter = converters.get(new BindId(beanType, formField.getType()));
            Validator.checkConverter(converter, formField, beanType);
            controller.converter = converter;
            controller.formField = formField;
            controller.beanGetter = BeanReflection.propertyGetterFor(path, beanClass);
            controller.formSetter = setters.get(formField.getType());
            controller.hide = formProperty.hideAfterClear();
            controller.defaultValue = formProperty.defaultValue();
            controller.hideCheckFields = hideCheckFields.computeIfAbsent(formField.getName(), k -> new ArrayList<>());
            formHandlers.add(controller);
        }
    }

    @SuppressWarnings("unchecked") //Should be checked on addHandling generics
    public void clearForm(Object form) {
        controllers.keySet().stream().filter(key -> key.left.equals(form.getClass()))
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
            controller.hideCheckFields.forEach(field -> hideBoundedSiblingField(field, form, controller));

            Object convertedValue = controller.converter.fromDefaultValue(controller.defaultValue);
            controller.formSetter.accept(element, convertedValue);
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
            Object beanFieldValue = controller.beanGetter.apply(bean);
            if (beanFieldValue != null) {
                Object formFieldElement = BeanReflection.getValueFromField(controller.formField, form, this::rethrow);
                Object formFieldValue = controller.converter.getFormConverter().apply(beanFieldValue);
                controller.formSetter.accept(formFieldElement, formFieldValue);
                visibilitySetters.get(formFieldElement.getClass()).accept(formFieldElement, true);
                controller.hideCheckFields.stream()
                        .map(hiddenField -> BeanReflection.getValueFromField(hiddenField, form, this::rethrow))
                        .forEach(t -> setVisible(t, controller));
            }
        }
    }

    @SuppressWarnings("unchecked") //Should be checked on addHandling generics
    private void setVisible(Object element, PropertyController controller) {
        visibilitySetters.get(element.getClass()).accept(element, !controller.hide);
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
