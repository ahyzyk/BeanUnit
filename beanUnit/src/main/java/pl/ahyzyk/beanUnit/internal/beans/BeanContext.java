package pl.ahyzyk.beanUnit.internal.beans;

import pl.ahyzyk.beanUnit.internal.BeanManagerContext;
import pl.ahyzyk.beanUnit.internal.TestBean;
import pl.ahyzyk.beanUnit.internal.TestBeanManager;
import pl.ahyzyk.beanUnit.producers.BeanProducer;
import pl.ahyzyk.beanUnit.producers.PersistenceContextProducer;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ahyzyk on 29.04.2017.
 */
public class BeanContext {
    private static Map<Class, Bean<?>> beanMap = new HashMap<>();
    private static TestBeanManager testBeanManager;

    public static Bean<?> getBean(Class aClass) {
        if (!beanMap.containsKey(aClass)) {
            beanMap.put(aClass, new BeanImpl<Object>(aClass));
        }
        return beanMap.get(aClass);
    }

    public static void fillBean(Object beanObject, Class clazz) {
        Bean<?> bean = getBean(clazz);
        for (InjectionPoint ip : bean.getInjectionPoints()) {
            try {
                inject(beanObject, ip);
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
        if (((Field) ip.getMember()).getType() == EntityManager.class) {
            return new PersistenceContextProducer().createEntityManager(ip);
        } else {
            Object result = new BeanProducer().defaultProducer(ip);
            if (result instanceof TestBean) {
                BeanManagerContext.add(((TestBean) result).getBeanClass(), (TestBean) result);
                fillBean(((TestBean) result).getSpy(), ((TestBean) result).getBeanClass());
            }
            return result;
        }

    }

    private static void inject(InjectionPoint ip, Object object, Object injectObject) {
        Field field = ((Field) ip.getMember());
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
