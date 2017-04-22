package pl.ahyzyk.beanUnit.annotations.utils;

import java.lang.annotation.Annotation;

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
}
