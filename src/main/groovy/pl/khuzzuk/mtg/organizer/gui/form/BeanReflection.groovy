package pl.khuzzuk.mtg.organizer.gui.form

import java.lang.reflect.Method
import java.util.function.Function

class BeanReflection {
    private static final Class<?>[] BASIC_TYPES = [
            boolean.class,
            byte.class,
            short.class,
            int.class,
            long.class,
            float.class,
            double.class,
            String.class,
            URL.class,
    ]

    static boolean hasGetterForPath(String path, Class<?> beanType) {
        def pathElements = getPathElement(path) as String[]
        Class<?> currentType = beanType
        for (int i = 0; i < pathElements.length; i++) {
            if (currentType == null) return false

            def currentGetter = getterMethodFor(pathElements[i], currentType)
            if (currentGetter == null) return false

            currentType = currentGetter.returnType
            if (currentType in BASIC_TYPES) return i == pathElements.length - 1
        }
        false
    }

    static Method getterMethodFor(String fieldName, Class<?> beanType) {
        String methodName = 'get' + fieldName.charAt(0).toUpperCase() + fieldName.substring(1)
        Method method = null
        Class<?> currentType = beanType
        while (Object.class != currentType && method == null) {
            try {
                method = currentType.getDeclaredMethod(methodName)
            } catch (NoSuchMethodException ignore) {
                currentType = currentType.getSuperclass()
            }
        }
        method
    }

    static <T> Function<T, ?> propertyGetterFor(String path, Class<T> beanType) {
        def pathElements = getPathElement(path)
        List<Method> methodChain = new ArrayList<>()
        Class<?> currentType = beanType
        for (int i = 0; i < pathElements.length; i++) {
            def currentGetter = getterMethodFor(pathElements[i], currentType)
            if (currentGetter == null) throw new IllegalArgumentException(String.format('cannot find getter for element: %s in path: %s', pathElements[i], path))
            methodChain.add(currentGetter)
            currentType = currentGetter.returnType
        }
        createPropertyGetterWith(methodChain)
    }

    static Class<?> getFieldTypeFor(String path, Class<?> beanType) {
        def pathElements = getPathElement(path)
        Class<?> currentType = beanType
        for (int i = 0; i < pathElements.length; i++) {
            def currentGetter = getterMethodFor(pathElements[i], currentType)
            if (currentGetter == null) throw new IllegalArgumentException(String.format('cannot find getter for element: %s in path: %s', pathElements[i], path))
            currentType = currentGetter.returnType
        }
        currentType
    }

    static <T> Function<T, ?> createPropertyGetterWith(List<Method> methodsChain) {
        return { T bean ->
            Object currentValue = bean
            for (Method method : methodsChain) {
                currentValue = method.invoke(currentValue)
            }
            return currentValue
        }
    }

    static String[] getPathElement(String path) {
        return path?.contains('.') ? path.split('\\.') : [path] as String[]
    }
}
