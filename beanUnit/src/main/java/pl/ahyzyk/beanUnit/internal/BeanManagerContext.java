package pl.ahyzyk.beanUnit.internal;

import java.util.Map;

/**
 * Created by ahyzyk on 29.04.2017.
 */
public class BeanManagerContext {
    private static BeanManager beanManager = new BeanManager();

    public static BeanManager getCurrent() {
        return beanManager;
    }

    public static void clear() {
        beanManager.clear();
    }


    public static void add(Class clazz, TestBean testBean) {
        beanManager.add(clazz, testBean);
    }

    public static void addImplementation(Class clazz, Class implementation) {
        beanManager.addImplementation(clazz, implementation);
    }

    public static TestBean get(Class clazz) {
        return beanManager.get(clazz);
    }

    public static Class getImplementation(Class clazz) {
        return beanManager.getImplementation(clazz);
    }

    public static Map<Class, TestBean> getBeans() {
        return beanManager.getBeans();
    }
}
