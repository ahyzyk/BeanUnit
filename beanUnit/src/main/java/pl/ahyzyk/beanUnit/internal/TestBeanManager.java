package pl.ahyzyk.beanUnit.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ahyzyk.beanUnit.annotations.BeanImplementations;
import pl.ahyzyk.beanUnit.annotations.utils.AnnotationUtils;
import pl.ahyzyk.beanUnit.beans.SessionContextImpl;
import pl.ahyzyk.beanUnit.beans.UserTransactionImpl;
import pl.ahyzyk.beanUnit.internal.beans.BeanContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.transaction.UserTransaction;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;
import java.util.function.Function;

import static pl.ahyzyk.beanUnit.annotations.utils.AnnotationUtils.getAnnotation;


public class TestBeanManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestBeanManager.class);
    private Stack<TestBean> constructed = new Stack<>();
    private BeanState beanState;

    private TransactionStatus transactionStatus = TransactionStatus.NONE;


    private Stack<TransactionStatus> transactionStatusStack;

    public static void callMethod(TestBean object, boolean supperBeforeClass, Function<Method, Boolean> filter, Object[] parameters) throws InvocationTargetException, IllegalAccessException {
        callMethod(object, object.getBean(), object.getBeanClass(), supperBeforeClass, filter, parameters);
    }

    public static void callMethod(Object object, Class<?> aClass, boolean supperBeforeClass, Function<Method, Boolean> filter, Object[] parameters) throws InvocationTargetException, IllegalAccessException {
        callMethod(null, object, aClass, supperBeforeClass, filter, parameters);
    }

    public static void callMethod(TestBean bean, Object object, Class<?> aClass, boolean supperBeforeClass, Function<Method, Boolean> filter, Object[] parameters) throws InvocationTargetException, IllegalAccessException {
        if (aClass == Object.class) {
            return;
        }
        if (supperBeforeClass) {
            callMethod(bean, object, aClass.getSuperclass(), supperBeforeClass, filter, parameters);
        }

        for (Method method : aClass.getDeclaredMethods()) {
            if (filter.apply(method)) {
                method.setAccessible(true);
                if (bean != null) {
                    TestProxyHandler.invokeStatic(bean, object, method, parameters);
                } else {
                    method.invoke(object, parameters);
                }
            }
        }


        if (!supperBeforeClass) {
            callMethod(bean, object, aClass.getSuperclass(), supperBeforeClass, filter, parameters);
        }


    }

    public void init(Object object) {
        BeanManagerContext.clear();

        TestPersistenceContext.getInstance().endAll();
        TransactionAttribute temp = AnnotationUtils.getAnnotation(object.getClass(), TransactionAttribute.class);
        transactionStatus = temp != null ? getTransactionStatus(temp.value()) : TransactionStatus.NONE;
        transactionStatusStack = new Stack<>();
        beanState = BeanState.CREATE;
        constructed.clear();
        initDefaultInjects();
        initImplementations(object);

        BeanContext.fillBean(object, null, object.getClass());


    }

    private void initImplementations(Object object) {
        try {
            callMethod(null, object, object.getClass(), true, m -> m.isAnnotationPresent(BeanImplementations.class), new Object[]{BeanManagerContext.getCurrent()});
        } catch (Throwable e) {
            new RuntimeException("Error during loading implementations", e);
        }
    }

    private void initDefaultInjects() {
        BeanManagerContext.add(Integer.class, new TestBean(1));
        BeanManagerContext.add(Long.class, new TestBean(1L));
        BeanManagerContext.add(String.class, new TestBean(""));
        BeanManagerContext.add(BeanManager.class, new TestBean(BeanManagerContext.getCurrent()));
        BeanManagerContext.addImplementation(UserTransaction.class, UserTransactionImpl.class);
        BeanManagerContext.addImplementation(SessionContext.class, SessionContextImpl.class);
    }

    public void constructBean(TestBean bean) {
        if (beanState == BeanState.CREATE || bean.isConstructed()) {
            return;
        }
        try {
            bean.setConstructed();
            callMethod(bean, true, m -> m.isAnnotationPresent(PostConstruct.class), null);

        } catch (Exception e) {
            throw new RuntimeException("Error during postConstruct call", e);
        }
    }


    private TestBean findInjectObject(Field field) throws IllegalAccessException, InstantiationException {
        Class clazz = field.getType();

        if (BeanManagerContext.get(clazz) == null) {
            Class implementation = BeanManagerContext.getImplementation(clazz);
            Object result = implementation.newInstance();
            TestBean bean = new TestBean(result, this);
            BeanManagerContext.add(clazz, bean);
            BeanContext.fillBean(bean.getSpy(), bean.getObject(), bean.getBeanClass());
        }
        return BeanManagerContext.get(clazz);
    }

    public void destroy() {
        while (!constructed.isEmpty()) {
            TestBean bean = constructed.pop();
            try {
                //singleton shouldn't be destroyed
                if (getAnnotation(bean.getBeanClass(), Singleton.class) == null) {
                    callMethod(bean, false, m -> m.isAnnotationPresent(PreDestroy.class), null);
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to call PreDestroy " + bean.getBeanClass(), e);
            }
        }
    }


    public void addConstructed(TestBean testBean) {
        constructed.add(testBean);
    }

    public void beginTransaction(boolean transactional) {
        TestPersistenceContext.getInstance().begin(transactional);

    }

    public void endTransaction() {
        TestPersistenceContext.getInstance().end();
    }

    public void endTransactions() {
        TestPersistenceContext.getInstance().endAll();

    }


    public void closeEntityManagers() {
        TestPersistenceContext.getInstance().close();
    }

    public void initStartup() {
        BeanManagerContext.getBeans().values().forEach(this::initStartup);
    }

    private void initStartup(TestBean testBean) {
        if (getAnnotation(testBean.getBeanClass(), Startup.class) != null) {
            testBean.constructBean();
        }
    }


    private TransactionStatus getTransactionStatus(TransactionAttributeType transactionAttributeType) {
        switch (transactionAttributeType) {
            case MANDATORY:
                if (!transactionStatus.isTransactional()) {
                    throw new RuntimeException("MANDATORY without transaction");
                }
            case REQUIRED:
                if (!transactionStatus.isTransactional()) {
                    return TransactionStatus.CREATE_NEW;
                } else {
                    return TransactionStatus.ACTIVE;
                }

            case REQUIRES_NEW:
                return TransactionStatus.CREATE_NEW;

            case SUPPORTS:
                break;
            case NOT_SUPPORTED:
                if (transactionStatus.isTransactional()) {
                    return transactionStatus.TO_NONE;
                } else {
                    return TransactionStatus.NONE;
                }

            case NEVER:
                if (transactionStatus.isTransactional()) {
                    throw new RuntimeException("NEVER with transaction");
                }
                return TransactionStatus.NONE;

        }
        return TransactionStatus.NONE;
    }


    public void popTransaction() {
        if (transactionStatus.isCreateNewEntityManager()) {
            endTransaction();
        }
        transactionStatus = transactionStatusStack.pop();
    }

    public void pushTransaction(TransactionAttributeType transactionAttributeType) {
        transactionStatusStack.push(transactionStatus);
        transactionStatus = getTransactionStatus(transactionAttributeType);
        if (transactionStatus.isCreateNewEntityManager()) {
            beginTransaction(transactionStatus.isTransactional());
        }

    }


    public void setBeanState(BeanState beanState) {
        this.beanState = beanState;
    }
}
