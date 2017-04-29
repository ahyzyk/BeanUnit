package pl.ahyzyk.beanUnit.beans;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;
import java.util.Iterator;

/**
 * Created by ahyzyk on 29.04.2017.
 */
public class TestInstance<T> implements Instance<T> {
    @Override
    public Instance<T> select(Annotation... qualifiers) {
        return null;
    }

    @Override
    public <U extends T> Instance<U> select(Class<U> subtype, Annotation... qualifiers) {
        return null;
    }

    @Override
    public <U extends T> Instance<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
        return null;
    }

    @Override
    public boolean isUnsatisfied() {
        return false;
    }

    @Override
    public boolean isAmbiguous() {
        return false;
    }

    @Override
    public void destroy(T instance) {

    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public T get() {
        return null;
    }
}
