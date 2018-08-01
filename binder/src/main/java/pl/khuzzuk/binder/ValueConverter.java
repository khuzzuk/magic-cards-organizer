package pl.khuzzuk.binder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ValueConverter<T, U> {
    public static final Supplier EMPTY_SUPPLIER = () -> null;
    public static final ValueConverter DEFAULT_CONVERTER = new ValueConverter(Function.identity(), Function.identity(), Function.identity(), EMPTY_SUPPLIER);

    private Function<T, U> formConverter;
    private Function<U, T> beanConverter;
    private Function<String, U> fromDefaultValue;
    private Supplier<U> fromEmpty;

    public static <T, U> ValueConverter<T, U> create(
            Function<T, U> formConverter,
            Function<U, T> beanConverter) {
        return new ValueConverter<>(formConverter, beanConverter, null, EMPTY_SUPPLIER);
    }

    public static <T, U> ValueConverter<T, U> create(
            Function<T, U> formConverter,
            Function<U, T> beanConverter,
            Function<String, U> fromDefaultValue) {
        return new ValueConverter<>(formConverter, beanConverter, fromDefaultValue, EMPTY_SUPPLIER);
    }

    public static <T> ValueConverter<T, String> create(
            Function<T, String> formConverter,
            Function<String, T> beanConverter,
            String defaultValue) {
        return new ValueConverter<>(formConverter, beanConverter, null, () -> defaultValue);
    }

    public static <T, U> ValueConverter<T, U> create(
            Function<T, U> formConverter,
            Function<U, T> beanConverter,
            Function<String, U> fromDefaultValue,
            Supplier<U> fromEmpty) {
        return new ValueConverter<>(formConverter, beanConverter, fromDefaultValue, fromEmpty);
    }

    U fromDefaultValue(String defaultValue) {
        if (fromDefaultValue == null || defaultValue == null || defaultValue.length() == 0) return fromEmpty.get();
        return fromDefaultValue.apply(defaultValue);
    }
}
