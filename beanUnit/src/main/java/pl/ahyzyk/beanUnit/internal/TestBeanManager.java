package pl.ahyzyk.beanUnit.internal;

import org.junit.runners.model.FrameworkMethod;
import pl.ahyzyk.beanUnit.ConnectionHelper;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.PostActivate;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class TestBeanManager {
    public static Map<Class, TestBean> beans = new HashMap<>();


    public static void init(FrameworkMethod method, Object object, ConnectionHelper connectionHelper) {
        beans.clear();
        initDefaultInjects(connectionHelper);
        analyzeFields(object, object.getClass());
    }

    private static void initDefaultInjects(ConnectionHelper connectionHelper) {
        beans.put(Integer.class, new TestBean(1, true));
        beans.put(Long.class, new TestBean(1L, true));
        beans.put(String.class, new TestBean("", true));
        beans.put(EntityManager.class, new TestBean(connectionHelper.getEntityManager(), true));
    }

    public static void constructBean(TestBean bean) {
        if (bean.isConstructed()) {
            return;
        }
        System.out.println(bean.getObject());
        try {
            findPostConstruct(bean.getObject(), bean.getObject().getClass());
            bean.setConstructed(true);
        } catch (Exception e) {
            throw new RuntimeException("unable to call postContruct", e);
        }
    }


    private static void findPostConstruct(Object object, Class<?> aClass) throws InvocationTargetException, IllegalAccessException {
        if (aClass == Object.class) {
            return;
        }

        findPostConstruct(object, aClass.getSuperclass());

        for (Method method : aClass.getDeclaredMethods()) {
            if (isPostConstructAnnotationPresent(method)) {
                method.setAccessible(true);
                System.out.println("PostConstructs : " + method);
                method.invoke(object, (Object[]) null);
            }
        }

    }

    private static boolean isPostConstructAnnotationPresent(Method method) {
        return method.isAnnotationPresent(PostConstruct.class) || method.isAnnotationPresent(PostActivate.class);
    }

    private static void analyzeFields(Object object, Class clazz) {
        for (Field field : clazz.getDeclaredFields()) {

            if (isInjectAnnotationPresent(field)) {
                field.setAccessible(true);
                try {
                    field.set(object, findInjectObject(field).getBean());
                } catch (Exception e) {
                    throw new RuntimeException("error", e);
                }
            }
        }
        if (clazz.getSuperclass() != Object.class) {
            analyzeFields(object, clazz.getSuperclass());
        }
    }

    private static boolean isInjectAnnotationPresent(Field field) {
        return field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(EJB.class) || field.isAnnotationPresent(PersistenceContext.class);
    }

    private static TestBean findInjectObject(Field field) throws IllegalAccessException, InstantiationException {
        Class clazz = field.getType();

        if (!beans.containsKey(clazz)) {
            Object result = clazz.newInstance();
            TestBean bean = new TestBean(result);
            beans.put(clazz, bean);
            analyzeFields(bean.getSpy(), bean.getObject().getClass());
        }
        return beans.get(clazz);
    }
}
