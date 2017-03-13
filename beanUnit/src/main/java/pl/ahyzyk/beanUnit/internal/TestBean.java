package pl.ahyzyk.beanUnit.internal;

import org.mockito.cglib.proxy.Enhancer;

import static org.mockito.Mockito.spy;


public class TestBean {
    private final Object spy;
    private Object object;
    private Object bean;
    private boolean constructed;

    public TestBean(Object object) {
        this.object = object;
        this.spy = spy(object);
        constructed = false;
        createBean();
    }

    public TestBean(Object object, boolean constructed) {
        this.object = object;
        this.spy = object;
        this.constructed = constructed;
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


    public void setConstructed() {
        this.constructed = true;
    }
}
