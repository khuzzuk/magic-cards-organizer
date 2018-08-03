package pl.khuzzuk.binder;

import java.lang.reflect.Field;

class Validator {
    static void checkConverter(ValueConverter<?, ?> converter, Field formField, Class<?> beanType) {
        if (converter == null) {
            throw new IllegalArgumentException(String.format("\nNo converter defined for field: %s\nand bean type:%s",
                    formField, beanType));
        }
    }
}
