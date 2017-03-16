package pl.ahyzyk.beanUnit.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrz on 16.03.2017.
 */
public class BeanManager {
    private Map<Class, TestBean> beans = new HashMap<>();

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


}
