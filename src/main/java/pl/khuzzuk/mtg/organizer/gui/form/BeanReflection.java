package pl.khuzzuk.mtg.organizer.gui.form;

import lombok.experimental.UtilityClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@UtilityClass
public class BeanReflection {
    private static final List<Class<?>> BASIC_TYPES = List.of(
            boolean.class,
            byte.class,
            short.class,
            int.class,
            long.class,
            float.class,
            double.class,
            String.class,
            URL.class);

    public static boolean hasGetterForPath(String path, Class<?> beanType) {
        String[] pathElements = getPathElement(path);
        Class<?> currentType = beanType;
        for (int i = 0; i < pathElements.length; i++) {
            if (currentType == null) return false;

            Method currentGetter = getterMethodFor(pathElements[i], currentType);
            if (currentGetter == null) return false;

            currentType = currentGetter.getReturnType();
            if (BASIC_TYPES.contains(currentType))return i == pathElements.length - 1;
        }

        return false;
    }

    private static Method getterMethodFor(String fieldName, Class<?> beanType) {
        String methodName = "get" + String.valueOf(fieldName.charAt(0)).toUpperCase() + fieldName.substring(1);
        Method method = null;
        Class<?> currentType = beanType;
        while (!Object.class.equals(currentType) && method == null) {
            try {
                method = currentType.getDeclaredMethod(methodName);
            } catch (NoSuchMethodException ignore) {
                currentType = currentType.getSuperclass();
            }

        }

        return method;
    }

    public static <T> Function<T, ?> propertyGetterFor(String path, Class<T> beanType) {
        String[] pathElements = getPathElement(path);
        List<Method> methodChain = new ArrayList<>();
        Class<?> currentType = beanType;
        for (String pathElement : pathElements) {
            Method currentGetter = getterMethodFor(pathElement, currentType);
            if (currentGetter == null) {
                throw new IllegalArgumentException(String.format("cannot find getter for element: %s in path: %s", pathElement, path));
            }
            methodChain.add(currentGetter);
            currentType = currentGetter.getReturnType();
        }

        return createPropertyGetterWith(methodChain);
    }

    public static Class<?> getFieldTypeFor(String path, Class<?> beanType) {
        String[] pathElements = getPathElement(path);
        Class<?> currentType = beanType;
        for (String pathElement : pathElements) {
            Method currentGetter = getterMethodFor(pathElement, currentType);
            if (currentGetter == null) {
                throw new IllegalArgumentException(String.format("cannot find getter for element: %s in path: %s", pathElement, path));
            }
            currentType = currentGetter.getReturnType();
        }

        return currentType;
    }

    private static <T> Function<T, ?> createPropertyGetterWith(final List<Method> methodsChain) {
        return bean -> {
                Object currentValue = bean;
                for (Method method : methodsChain) {
                    try {
                        currentValue = method.invoke(currentValue);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                return currentValue;
            };
    }

    private static String[] getPathElement(String path) {
        return path.contains(".") ? path.split("\\.") : new String[]{path};
    }
}
