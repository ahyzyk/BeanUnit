package pl.ahyzyk.beanUnit.internal;

import org.mockito.cglib.proxy.Enhancer;

import static org.mockito.Mockito.spy;


public class TestBean {
    private final Object spy;
    private final TestBeanManager manager;
    private Object object;
    private Object bean;
    private boolean constructed;

    public TestBean(Object object, TestBeanManager manager) {
        this.object = object;
        this.spy = spy(object);
        constructed = false;
        this.manager = manager;
        createBean();
    }

    public TestBean(Object object) {
        this.object = object;
        this.spy = object;
        this.constructed = true;
        this.manager = null;
        bean = object;
    }

    private void createBean() {
        Enhancer e = new Enhancer();
        e.setSuperclass(object.getClass());
        e.setClassLoader(object.getClass().getClassLoader());
        e.setCallback(new TestProxyHandler(this));
        bean = e.create();
    }


    public Object getBean() {
        return bean;
    }

    protected Object getObject() {
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
        manager.addConstucted(this);
        this.constructed = true;
    }

    public void constructBean() {
        try {
            if (manager != null) {
                manager.constructBean(this);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error during constructing bean : " + this.getObject().getClass(), ex);
        }
    }
}
