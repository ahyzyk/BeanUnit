package pl.ahyzyk.beanUnit.internal;

import pl.ahyzyk.beanUnit.ConnectionHelper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;


public class TestBeanManager {
    private Map<Class, TestBean> beans = new HashMap<>();
    private Stack<TestBean> constucted = new Stack<>();

    public static void callMethod(TestBean object, boolean supperBeforeClass, Function<Method, Boolean> filter) throws InvocationTargetException, IllegalAccessException {
        callMethod(object.getSpy(), object.getObject().getClass(), supperBeforeClass, filter);
    }

    public static void callMethod(Object object, Class<?> aClass, boolean supperBeforeClass, Function<Method, Boolean> filter) throws InvocationTargetException, IllegalAccessException {
        if (aClass == Object.class) {
            return;
        }
        if (supperBeforeClass) {
            callMethod(object, aClass.getSuperclass(), supperBeforeClass, filter);
        }

        for (Method method : aClass.getDeclaredMethods()) {
            if (filter.apply(method)) {
                method.setAccessible(true);
                method.invoke(object, (Object[]) null);
            }
        }


        if (!supperBeforeClass) {
            callMethod(object, aClass.getSuperclass(), supperBeforeClass, filter);
        }


    }

    private static boolean isInjectAnnotationPresent(Field field) {
        return field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(EJB.class) || field.isAnnotationPresent(PersistenceContext.class);
    }

    public void init(Object object, ConnectionHelper connectionHelper) {
        beans.clear();
        initDefaultInjects(connectionHelper);
        analyzeFields(object, object.getClass());

    }

    private void initDefaultInjects(ConnectionHelper connectionHelper) {
        beans.put(Integer.class, new TestBean(1));
        beans.put(Long.class, new TestBean(1L));
        beans.put(String.class, new TestBean(""));
        beans.put(TestBeanManager.class, new TestBean(this));
        beans.put(EntityManager.class, new TestBean(connectionHelper.getEntityManager()));
    }

    public void constructBean(TestBean bean) {
        if (bean.isConstructed()) {
            return;
        }
        try {
            callMethod(bean, true, m -> m.isAnnotationPresent(PostConstruct.class));
            bean.setConstructed();
        } catch (Exception e) {
            throw new RuntimeException("Error during postConstruct call", e);
        }
    }

    public void analyzeFields(Object object, Class clazz) {
        for (Field field : clazz.getDeclaredFields()) {

            if (isInjectAnnotationPresent(field)) {
                field.setAccessible(true);
                try {
                    field.set(object, findInjectObject(field).getBean());
                } catch (Exception e) {
                    throw new RuntimeException("Error during injecting bean into " + field.toString(), e);
                }
            }
        }
        if (clazz.getSuperclass() != Object.class) {
            analyzeFields(object, clazz.getSuperclass());
        }
    }

    private TestBean findInjectObject(Field field) throws IllegalAccessException, InstantiationException {
        Class clazz = field.getType();

        if (!beans.containsKey(clazz)) {
            Object result = clazz.newInstance();
            TestBean bean = new TestBean(result, this);
            beans.put(clazz, bean);
            analyzeFields(bean.getSpy(), bean.getObject().getClass());
        }
        return beans.get(clazz);
    }

    public void destory() {
        while (!constucted.isEmpty()) {
            TestBean bean = constucted.pop();
            try {
                callMethod(bean, false, m -> m.isAnnotationPresent(PreDestroy.class));
            } catch (Exception e) {
                throw new RuntimeException("Unable to call PreDestroy " + bean.getObject().getClass());
            }
        }
    }


    public void addConstucted(TestBean testBean) {
        constucted.add(testBean);
    }
}
