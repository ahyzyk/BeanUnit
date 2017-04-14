package pl.ahyzyk.beanUnit.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ahyzyk.beanUnit.annotations.BeanImplementations;
import pl.ahyzyk.beanUnit.annotations.utils.AnnotationUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;
import java.util.function.Function;

import static pl.ahyzyk.beanUnit.annotations.utils.AnnotationUtils.getAnnotation;


public class TestBeanManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestBeanManager.class);
    private BeanManager beanManager = new BeanManager();
    private Stack<TestBean> constucted = new Stack<>();
    private TestPersistenceContext persistanceContext;
    private BeanState beanState;

    private TransactionStatus transactionStatus = TransactionStatus.NONE;


    private Stack<TransactionStatus> transactionStatusStack;

    public static void callMethod(TestBean object, boolean supperBeforeClass, Function<Method, Boolean> filter, Object[] parameters) throws InvocationTargetException, IllegalAccessException {
        callMethod(object.getSpy(), object.getBeanClass(), supperBeforeClass, filter, parameters);
    }

    public static void callMethod(Object object, Class<?> aClass, boolean supperBeforeClass, Function<Method, Boolean> filter, Object[] parameters) throws InvocationTargetException, IllegalAccessException {
        if (aClass == Object.class) {
            return;
        }
        if (supperBeforeClass) {
            callMethod(object, aClass.getSuperclass(), supperBeforeClass, filter, parameters);
        }

        for (Method method : aClass.getDeclaredMethods()) {
            if (filter.apply(method)) {
                method.setAccessible(true);
                method.invoke(object, parameters);
            }
        }


        if (!supperBeforeClass) {
            callMethod(object, aClass.getSuperclass(), supperBeforeClass, filter, parameters);
        }


    }

    private static boolean isInjectAnnotationPresent(Field field) {
        return field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(EJB.class) || field.isAnnotationPresent(PersistenceContext.class) ||
                field.isAnnotationPresent(Resource.class);
    }

    public void init(Object object) {
        beanManager.clear();
        TransactionAttribute temp = AnnotationUtils.getAnnotation(object.getClass(), TransactionAttribute.class);
        transactionStatus = temp != null ? getTransactionStatus(temp.value()) : TransactionStatus.NONE;
        transactionStatusStack = new Stack<>();
        beanState = BeanState.CREATE;
        constucted.clear();
        initDefaultInjects();
        initImplementations(object);

        analyzeFields(object, object.getClass());
        beanState = BeanState.CONSTRUCT;
    }

    private void initImplementations(Object object) {
        try {
            callMethod(object, object.getClass(), true, m -> m.isAnnotationPresent(BeanImplementations.class), new Object[]{beanManager});
        } catch (Throwable e) {
            new RuntimeException("Error during loading implementations", e);
        }
    }

    private void initDefaultInjects() {
        beanManager.add(Integer.class, new TestBean(1));
        beanManager.add(Long.class, new TestBean(1L));
        beanManager.add(String.class, new TestBean(""));
        beanManager.add(BeanManager.class, new TestBean(beanManager));
    }

    public void constructBean(TestBean bean) {
        if (beanState == BeanState.CREATE || bean.isConstructed()) {
            return;
        }
        try {
            callMethod(bean, true, m -> m.isAnnotationPresent(PostConstruct.class), null);
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
                    PersistenceContext persistenceAnnotation = field.getAnnotation(PersistenceContext.class);
                    inject(field, object, new TestEntityManager(persistanceContext, persistenceAnnotation.unitName()));
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
            TestBean testBean = findInjectObject(field);
            inject(field, object, testBean.getBean());
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
            Class implementation = beanManager.getImplementation(clazz);
            Object result = implementation.newInstance();
            TestBean bean = new TestBean(result, this);
            beanManager.add(clazz, bean);
            analyzeFields(bean.getSpy(), bean.getBeanClass());
        }
        return beanManager.get(clazz);
    }

    public void destroy() {
        while (!constucted.isEmpty()) {
            TestBean bean = constucted.pop();
            try {
                //singleton shouldn't be destroyed
                if (getAnnotation(bean.getBeanClass(), Singleton.class) == null) {
                    callMethod(bean, false, m -> m.isAnnotationPresent(PreDestroy.class), null);
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to call PreDestroy " + bean.getBeanClass());
            }
        }
    }


    public void addConstructed(TestBean testBean) {
        constucted.add(testBean);
    }

    public void beginTransaction() {
        persistanceContext.begin();

    }

    public void endTransaction() {
        persistanceContext.end();

    }

    public TestPersistenceContext getPersistenceContext() {
        return persistanceContext;
    }

    public void setPersistenceContext(TestPersistenceContext persistenceContext) {
        this.persistanceContext = persistenceContext;
    }

    public void closeEntityManagers() {
        persistanceContext.close();
    }

    public void initStartup() {
        beanManager.getBeans().values().forEach(this::initStartup);
    }

    private void initStartup(TestBean testBean) {
        if (getAnnotation(testBean.getBeanClass(), Startup.class) != null) {
            testBean.constructBean();
        }
    }


    public void pushTransaction(TransactionAttributeType transactionAttributeType) {
        transactionStatusStack.push(transactionStatus);
        transactionStatus = getTransactionStatus(transactionAttributeType);
        if (transactionStatus == TransactionStatus.CREATE_NEW) {
            beginTransaction();
        }
        LOGGER.info("TransactionStatus in:" + transactionStatus);

    }

    private TransactionStatus getTransactionStatus(TransactionAttributeType transactionAttributeType) {
        switch (transactionAttributeType) {
            case MANDATORY:
                if (!transactionStatus.isTransaction()) {
                    throw new RuntimeException("MANDATORY without transaction");
                }
            case REQUIRED:
                if (!transactionStatus.isTransaction()) {
                    return TransactionStatus.CREATE_NEW;
                } else {
                    return TransactionStatus.ACTIVE;
                }

            case REQUIRES_NEW:
                return TransactionStatus.CREATE_NEW;

            case SUPPORTS:
                break;
            case NOT_SUPPORTED:
                if (transactionStatus.isTransaction()) {
                    return transactionStatus.TO_NONE;
                } else {
                    return TransactionStatus.NONE;
                }

            case NEVER:
                if (transactionStatus.isTransaction()) {
                    throw new RuntimeException("NEVER with transaction");
                }
                return TransactionStatus.NONE;

        }
        return TransactionStatus.NONE;
    }


    public void popTransaction() {
        if (transactionStatus == TransactionStatus.CREATE_NEW) {
            endTransaction();
        }
        LOGGER.info("TransactionStatus out:" + transactionStatus);
        transactionStatus = transactionStatusStack.pop();
    }


}
