package pl.ahyzyk.beanUnit.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrz on 16.03.2017.
 */
public class BeanManager {
    private Map<Class, TestBean> beans = new HashMap<>();
    private Map<Class, Class> implementations = new HashMap<>();
    protected void clear() {
        beans.clear();
    }


    protected void add(Class<?> klass, TestBean testBean) {
        beans.put(klass, testBean);
    }

    protected TestBean get(Class clazz) {
        return beans.get(clazz);
    }

    public <T> T getSpy(Class<T> klass) {
        return (T) beans.get(klass).getSpy();
    }

    public void addImplementation(Class interfaceClass, Class implementationClass) {
        if (!interfaceClass.isAssignableFrom(implementationClass)) {
            throw new RuntimeException(String.format("%s not implementing %s", implementationClass.getCanonicalName(), interfaceClass.getCanonicalName()));
        }
        implementations.put(interfaceClass, implementationClass);
    }

    public Class getImplementation(Class interfaceClass) {
        if (implementations.containsKey(interfaceClass)) {
            return implementations.get(interfaceClass);
        } else {
            return interfaceClass;
        }
    }

    public Map<Class, TestBean> getBeans() {
        return beans;
    }
}
