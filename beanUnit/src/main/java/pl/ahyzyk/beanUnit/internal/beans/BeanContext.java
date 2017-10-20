package pl.ahyzyk.beanUnit.internal.beans;

import pl.ahyzyk.beanUnit.annotations.utils.AnnotationUtils;
import pl.ahyzyk.beanUnit.internal.BeanManagerContext;
import pl.ahyzyk.beanUnit.internal.TestBean;
import pl.ahyzyk.beanUnit.internal.TestBeanManager;
import pl.ahyzyk.beanUnit.producers.BeanProducer;
import pl.ahyzyk.beanUnit.producers.DataSourceProducer;
import pl.ahyzyk.beanUnit.producers.InstanceProducer;
import pl.ahyzyk.beanUnit.producers.PersistenceContextProducer;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by ahyzyk on 29.04.2017.
 */
public class BeanContext {
    private static Map<Class, Bean<?>> beanMap = new HashMap<>();
    private static TestBeanManager testBeanManager;


    private static Map<Class, Function<InjectionPoint, Object>> producers = new HashMap<>();
    private static BeanProducer beanProducer = new BeanProducer();
    private static Function<InjectionPoint, Object> defaultProducer = (ip) -> beanProducer.defaultProducer(ip);


    static {
        clearProducers();
    }

    public static void clearProducers() {
        addProducer(new PersistenceContextProducer());
        addProducer(new InstanceProducer());
        addProducer(new DataSourceProducer());
    }

    public static void addProducer(Object producer) {
        AnnotationUtils.getAnnotatedMethods(producer.getClass(), Produces.class)
                .forEach(m -> addProducerMethod(m.getReturnType(), producer, m));
    }

    private static void addProducerMethod(Class type, Object object, Method m) {
        if (type != Object.class) {
            producers.put(type, (ip) -> callMethod(object, m, ip));
        }
    }

    private static Object callMethod(Object object, Method m, InjectionPoint ip) {
        try {
            return m.invoke(object, new Object[]{ip});
        } catch (Exception ex) {
            throw new RuntimeException("Error during calling" + m);
        }
    }


    public static Bean<?> getBean(Class aClass) {
        if (!beanMap.containsKey(aClass)) {
            beanMap.put(aClass, new BeanImpl<Object>(aClass));
        }
        return beanMap.get(aClass);
    }

    public static void fillBean(Object beanObject, Object realObject, Class clazz) {
        Bean<?> bean = getBean(clazz);
        for (InjectionPoint ip : bean.getInjectionPoints()) {
            try {
                inject(beanObject, ip);
                if (realObject != null) {
                    inject(realObject, ip);
                }
            } catch (Exception ex) {
                throw new RuntimeException("Error during setting field " + ip.getMember(), ex);
            }
        }
    }

    private static void inject(Object beanObject, InjectionPoint ip) throws IllegalAccessException, InstantiationException {
        Object injectObject = findInjectObject(ip);
        inject(ip, beanObject, injectObject);
    }

    private static Object findInjectObject(InjectionPoint ip) throws InstantiationException, IllegalAccessException {
        if (producers.containsKey((Class) ip.getType())) {
            return producers.get((Class) ip.getType()).apply(ip);
        } else {
            Object result = new BeanProducer().defaultProducer(ip);
            if (result instanceof TestBean) {
                BeanManagerContext.add(((TestBean) result).getBeanClass(), (TestBean) result);
                fillBean(((TestBean) result).getSpy(), ((TestBean) result).getObject(), ((TestBean) result).getBeanClass());
            }
            return result;
        }

    }

    private static void inject(InjectionPoint ip, Object object, Object injectObject) {
        Field field = ((Field) ip.getMember());
        field.setAccessible(true);
        try {
            field.set(object, injectObject);
        } catch (Exception e) {
            throw new RuntimeException("Error during injecting bean into " + field.toString(), e);
        }
    }

    public static TestBeanManager getTestBeanManager() {
        return testBeanManager;
    }

    public static void setTestBeanManager(TestBeanManager tbm) {
        testBeanManager = tbm;
    }
}
