package pl.khuzzuk.mtg.organizer.gui.form;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;

@Getter
@Setter
class ValueConverter<T, U, V> {
    static final ValueConverter DEFAULT_CONVERTER = new ValueConverter(Function.identity(), Function.identity(), null);
    private Function<T, U> formConverter;
    private Function<U, T> beanConverter;
    private V formDefaultValue;

    ValueConverter(Function<T, U> formConverter, Function<U, T> beanConverter, V formDefaultValue) {
        this.formConverter = formConverter;
        this.beanConverter = beanConverter;
        this.formDefaultValue = formDefaultValue;
    }

    ValueConverter(Function<T, U> formConverter, Function<U, T> beanConverter) {
        this(formConverter, beanConverter, null);
    }
}
