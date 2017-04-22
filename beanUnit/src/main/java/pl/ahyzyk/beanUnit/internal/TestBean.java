package pl.ahyzyk.beanUnit.internal;

import org.mockito.cglib.proxy.Enhancer;
import pl.ahyzyk.beanUnit.annotations.defaultAnotations.DefaultTransactionAttribute;
import pl.ahyzyk.beanUnit.annotations.defaultAnotations.DefaultTransactionManagement;
import pl.ahyzyk.beanUnit.annotations.utils.AnnotationUtils;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.lang.reflect.Method;

import static org.mockito.Mockito.spy;


public class TestBean {
    final TestBeanManager manager;
    private final Class beanClass;
    private Object spy;
    private Object object;
    private Object bean;
    private boolean constructed;
    private TransactionAttributeType transactionAttribute;

    public TestBean(Object object, TestBeanManager manager) {
        this.beanClass = object.getClass();
        this.object = object;
        this.spy = spy(object);
        findOutTransactionalAttributeType(beanClass);
        constructed = false;
        this.manager = manager;
        createBean();
    }


    public TestBean(Object object) {
        this.beanClass = object.getClass();
        this.object = object;
        this.spy = object;
        transactionAttribute = TransactionAttributeType.NEVER;
        this.constructed = true;
        this.manager = null;
        bean = object;
    }

    public TransactionAttributeType getTransactionAttributeType(Method method) {
        if (method.isAnnotationPresent(TransactionAttribute.class)) {
            return method.getAnnotation(TransactionAttribute.class).value();
        }
        return transactionAttribute;
    }

    private void findOutTransactionalAttributeType(Object object) {
        transactionAttribute = TransactionAttributeType.SUPPORTS;
        TransactionManagementType type = AnnotationUtils.getAnnotation(beanClass, TransactionManagement.class, DefaultTransactionManagement.getInstance()).value();
        if (type == TransactionManagementType.CONTAINER) {
            TransactionAttribute temp = AnnotationUtils.getAnnotation(beanClass, TransactionAttribute.class, DefaultTransactionAttribute.getInstance(beanClass));
            transactionAttribute = temp.value();
        } else {
            transactionAttribute = TransactionAttributeType.NOT_SUPPORTED;
        }
    }

    private void createBean() {
        Enhancer e = new Enhancer();
        e.setSuperclass(beanClass);
        e.setClassLoader(beanClass.getClassLoader());
        e.setCallback(new TestProxyHandler(this));
        bean = e.create();
    }


    public Object getBean() {
        return bean;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public Object getObject() {
        return object;
    }

    public Object getSpy() {
        return spy;
    }

    protected boolean isConstructed() {
        return constructed;
    }

    public boolean canBeConstructed() {
        return manager != null;
    }

    public void setConstructed() {
        manager.addConstructed(this);
        this.constructed = true;
    }

    public void constructBean() {
        try {
            if (manager != null) {
                manager.constructBean(this);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error during constructing bean : " + this.beanClass, ex);
        }
    }


}
