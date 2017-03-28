package pl.ahyzyk.beanUnit.internal;

import org.mockito.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

public class TestProxyHandler implements InvocationHandler {
    private final TestBean bean;

    public TestProxyHandler(TestBean bean) {
        this.bean = bean;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        bean.constructBean();
        method.setAccessible(true);
        return method.invoke(bean.getSpy(), args);
    }
}
