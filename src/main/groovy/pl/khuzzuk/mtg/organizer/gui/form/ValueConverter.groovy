package pl.khuzzuk.mtg.organizer.gui.form

import java.util.function.Function

class ValueConverter<T, U> {
    public static final DEFAULT_CONVERTER = new ValueConverter()
    Function<T, U> formConverter
    Function<U, T> beanConverter
    U formDefaultValue

    ValueConverter(Function<T, U> formConverter = {it as U}, Function<U, T> beanConverter = {it as T}, U formDefaultValue = null) {
        this.formConverter = formConverter
        this.beanConverter = beanConverter
        this.formDefaultValue = formDefaultValue
    }
}
