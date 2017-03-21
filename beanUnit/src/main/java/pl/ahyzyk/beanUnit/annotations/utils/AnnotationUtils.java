package pl.ahyzyk.beanUnit.annotations.utils;

import java.lang.annotation.Annotation;

/**
 * Created by ahyzyk on 21.03.2017.
 */
public class AnnotationUtils {
    public static boolean isAnnotationPresent(Class clazz, Class<? extends Annotation> annotation) {
        if (clazz == Object.class) {
            return false;
        }
        if (!clazz.isAnnotationPresent(annotation)) {
            return isAnnotationPresent(clazz.getSuperclass(), annotation);
        }
        return true;
    }
}
