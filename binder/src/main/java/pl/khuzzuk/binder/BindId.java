package pl.khuzzuk.binder;

import java.util.Objects;

class BindId {
    final Class<?> left;
    final Class<?> right;

    BindId(Class<?> left, Class<?> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BindId bindId = (BindId) o;
        return Objects.equals(left, bindId.left) &&
                Objects.equals(right, bindId.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(left, right);
    }
}
