package pl.khuzzuk.binder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

class PropertyController {
    private Field formField;
    private Function beanGetter;
    private BiConsumer formSetter;
    private ValueConverter converter;
    private boolean hide;
    private String defaultValue;
    private List<Field> hideCheckFields;

    void setFormField(Field formField) {
        this.formField = formField;
    }

    void setBeanGetter(Function beanGetter) {
        this.beanGetter = beanGetter;
    }

    void setFormSetter(BiConsumer formSetter) {
        this.formSetter = formSetter;
    }

    void setConverter(ValueConverter converter) {
        this.converter = converter;
    }

    void setHide(boolean hide) {
        this.hide = hide;
    }

    void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    void setHideCheckFields(List<Field> hideCheckFields) {
        this.hideCheckFields = hideCheckFields;
    }

    Field getFormField() {
        return formField;
    }

    Function getBeanGetter() {
        return beanGetter;
    }

    BiConsumer getFormSetter() {
        return formSetter;
    }

    ValueConverter getConverter() {
        return converter;
    }

    boolean isHide() {
        return hide;
    }

    String getDefaultValue() {
        return defaultValue;
    }

    List<Field> getHideCheckFields() {
        return hideCheckFields;
    }
}
