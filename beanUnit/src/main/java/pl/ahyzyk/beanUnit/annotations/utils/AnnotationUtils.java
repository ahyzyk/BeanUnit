package pl.ahyzyk.beanUnit.annotations.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ahyzyk on 21.03.2017.
 */
public class AnnotationUtils {
    public static <T extends Annotation> T getAnnotation(Class clazz, Class<T> annotation, T defaultValue) {
        T result = getAnnotation(clazz, annotation);
        return result == null ? defaultValue : result;
    }

    public static <T extends Annotation> T getAnnotation(Class clazz, Class<T> annotation) {
        if (clazz == Object.class) {
            return null;
        }
        if (!clazz.isAnnotationPresent(annotation)) {
            return getAnnotation(clazz.getSuperclass(), annotation);
        }
        return (T) clazz.getAnnotation(annotation);
    }


    public static List<Method> getAnnotatedMethods(Class clazz, Class<? extends Annotation> annotation) {
        if (clazz == Object.class) {
            return Collections.emptyList();
        }

        List<Method> result = Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(annotation))
                .map(m -> (Method) m)
                .collect(Collectors.toList());

        result.addAll(getAnnotatedMethods(clazz.getSuperclass(), annotation));

        return result;
    }
}
