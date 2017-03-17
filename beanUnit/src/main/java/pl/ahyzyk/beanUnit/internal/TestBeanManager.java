package pl.ahyzyk.beanUnit.internal;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;
import java.util.function.Function;


public class TestBeanManager {

    private BeanManager beanManager = new BeanManager();
    private Stack<TestBean> constucted = new Stack<>();
    private TestPersistanceContext persistanceContext;

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

    public void init(Object object) {
        beanManager.clear();
        constucted.clear();
        initDefaultInjects();
        analyzeFields(object, object.getClass());

    }

    private void initDefaultInjects() {
        beanManager.add(Integer.class, new TestBean(1));
        beanManager.add(Long.class, new TestBean(1L));
        beanManager.add(String.class, new TestBean(""));
        beanManager.add(BeanManager.class, new TestBean(beanManager));
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
                if (field.isAnnotationPresent(PersistenceContext.class)) {
                    PersistenceContext persistanceAnnotation = field.getAnnotation(PersistenceContext.class);
                    inject(field, object, persistanceContext.get(persistanceAnnotation.unitName()));
                } else {
                    injectObject(object, field);

                }
            }
        }
        if (clazz.getSuperclass() != Object.class) {
            analyzeFields(object, clazz.getSuperclass());
        }
    }


    private void injectObject(Object object, Field field) {
        try {
            Object beanObject = findInjectObject(field).getBean();
            inject(field, object, beanObject);
        } catch (Exception ex) {
            throw new RuntimeException("Error during searching bean field:" + field, ex);
        }

    }

    private void inject(Field field, Object object, Object beanObject) {
        try {
            field.set(object, beanObject);
        } catch (Exception e) {
            throw new RuntimeException("Error during injecting bean into " + field.toString(), e);
        }
    }

    private TestBean findInjectObject(Field field) throws IllegalAccessException, InstantiationException {
        Class clazz = field.getType();

        if (beanManager.get(clazz) == null) {
            Object result = clazz.newInstance();
            TestBean bean = new TestBean(result, this);
            beanManager.add(clazz, bean);
            analyzeFields(bean.getSpy(), bean.getObject().getClass());
        }
        return beanManager.get(clazz);
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

    public void beginTransaction() {
        persistanceContext.begin();

    }

    public void endTransaction() {
        persistanceContext.end();

    }

    public TestPersistanceContext getPersistanceContext() {
        return persistanceContext;
    }

    public void setPersistanceContext(TestPersistanceContext persistanceContext) {
        this.persistanceContext = persistanceContext;
    }

    public void closeEntityManagers() {
        persistanceContext.close();
    }
}
