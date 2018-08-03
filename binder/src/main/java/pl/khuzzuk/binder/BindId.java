package pl.khuzzuk.binder;

class BindId {
    final Class<?> left;
    final Class<?> right;

    BindId(Class<?> left, Class<?> right) {
        this.left = left;
        this.right = right;
    }
}
