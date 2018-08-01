package pl.khuzzuk.binder;

import lombok.Data;

@Data
class BindId {
    private final Class<?> left;
    private final Class<?> right;
}
