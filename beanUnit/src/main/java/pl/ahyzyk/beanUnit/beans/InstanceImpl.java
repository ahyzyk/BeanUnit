package pl.ahyzyk.beanUnit.beans;

import pl.ahyzyk.beanUnit.internal.BeanManagerContext;
import pl.ahyzyk.beanUnit.internal.TestBean;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ahyzyk on 29.04.2017.
 */
public class InstanceImpl<T> implements Instance<T> {

    private Class clazz;
    private List<TestBean> beans;

    public InstanceImpl(Class instances) {
        this.clazz = instances;
    }


    public InstanceImpl(List<TestBean> beans) {
        this.beans = beans;
    }


    private List<TestBean> getBeans() {
        if (beans != null) {
            return beans;
        }
        beans = BeanManagerContext.getBeans().entrySet().stream()
                .filter(b -> clazz.isAssignableFrom(b.getKey()))
                .map(b -> b.getValue())
                .collect(Collectors.toList());
        return beans;
    }


    @Override
    public Instance<T> select(Annotation... qualifiers) {
        Set<Annotation> annotations = new HashSet<>(Arrays.asList(qualifiers));

        return new InstanceImpl<>(getBeans().stream()
                .filter(i -> hasAnnotations(i.getBeanClass().getClass(), annotations))
                .collect(Collectors.toList()));

    }

    private boolean hasAnnotations(Class clazz, Set<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            if (!clazz.isAnnotationPresent(annotation.getClass())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public <U extends T> Instance<U> select(Class<U> subtype, Annotation... qualifiers) {
        Set<Annotation> annotations = new HashSet<>(Arrays.asList(qualifiers));
        return new InstanceImpl<U>(getBeans().stream()
                .filter(i -> subtype.isAssignableFrom(i.getBeanClass()) && hasAnnotations(i.getBeanClass(), annotations))
                .collect(Collectors.toList()));

    }

    @Override
    public <U extends T> Instance<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
        return null;
    }

    @Override
    public boolean isUnsatisfied() {
        return beans.isEmpty();
    }

    @Override
    public boolean isAmbiguous() {
        return beans.size() > 0;
    }

    @Override
    public void destroy(T instance) {

    }

    @Override
    public Iterator<T> iterator() {
        return (Iterator<T>) getBeans().stream().map(b -> b.getBean()).iterator();
    }

    @Override
    public T get() {
        return (T) getBeans().get(0).getBean();
    }
}
