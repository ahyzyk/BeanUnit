package pl.ahyzyk.beanUnit.producers;

import javax.enterprise.inject.spi.Annotated;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ahyzyk on 29.04.2017.
 */
public class AnnotatedMethodImpl implements Annotated {

    private final Field field;

    public AnnotatedMethodImpl(Field field) {
        this.field = field;
    }

    @Override
    public Type getBaseType() {
        return Field.class;
    }

    @Override
    public Set<Type> getTypeClosure() {
        //TODO do implementacji
        return null;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return field.getAnnotation(annotationType);
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return Arrays.stream(field.getAnnotations()).collect(Collectors.toSet());
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return field.isAnnotationPresent(annotationType);
    }
}
