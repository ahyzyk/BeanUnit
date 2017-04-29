package pl.ahyzyk.beanUnit.producers;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * Created by ahyzyk on 29.04.2017.
 */
public class InjectionPointImpl implements InjectionPoint {


    private final Class type;
    private final Field field;
    private final AnnotatedFieldImpl annotated;
    private final Bean<?> bean;

    public InjectionPointImpl(Field field, Bean<?> bean) {
        this.field = field;
        this.type = field.getType();
        this.annotated = new AnnotatedFieldImpl(field);
        this.bean = bean;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return null;
    }

    @Override
    public Bean<?> getBean() {
        return bean;
    }

    @Override
    public Member getMember() {
        return field;
    }

    @Override
    public Annotated getAnnotated() {
        return annotated;
    }

    @Override
    public boolean isDelegate() {
        return false;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InjectionPointImpl that = (InjectionPointImpl) o;

        return field != null ? field.equals(that.field) : that.field == null;
    }

    @Override
    public int hashCode() {
        return field != null ? field.hashCode() : 0;
    }
}
