package pl.ahyzyk.beanUnit.internal.beans;

import pl.ahyzyk.beanUnit.producers.InjectionPointImpl;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.PersistenceContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ahyzyk on 29.04.2017.
 */
public class BeanImpl<T> implements Bean<T> {
    private final Class<T> beanClass;
    private Set<InjectionPoint> injectionPoints = null;

    public BeanImpl(Class<T> beanClass) {
        this.beanClass = beanClass;
    }

    private static boolean isInjectAnnotationPresent(Field field) {
        return field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(EJB.class) || field.isAnnotationPresent(PersistenceContext.class) ||
                field.isAnnotationPresent(Resource.class);
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void analyzeFields(Class clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (isInjectAnnotationPresent(field)) {
                field.setAccessible(true);
                injectionPoints.add(new InjectionPointImpl(field, this));
            }
        }
        if (clazz.getSuperclass() != Object.class) {
            analyzeFields(clazz.getSuperclass());
        }
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        if (injectionPoints == null) {
            injectionPoints = new HashSet<>();
            analyzeFields(beanClass);
        }
        return injectionPoints;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        return null;
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {

    }

    @Override
    public Set<Type> getTypes() {
        return null;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return null;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return null;
    }

    @Override
    public boolean isAlternative() {
        return false;
    }
}
