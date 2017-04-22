package pl.ahyzyk.beanUnit.internal;

import org.mockito.cglib.proxy.InvocationHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestProxyHandler implements InvocationHandler {
    private final TestBean bean;

    public TestProxyHandler(TestBean bean) {
        this.bean = bean;
    }

    public static Object invokeStatic(TestBean bean, Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(bean.getSpy(), args);
        }
//        System.out.println("proxy:" +method);
        bean.constructBean();
        method.setAccessible(true);
        if (method.getDeclaringClass() != Object.class) {
            bean.manager.pushTransaction(bean.getTransactionAttributeType(method));
        }
        Object result;
        try {
            result = method.invoke(bean.getSpy(), args);
        } catch (Exception ex) {
            TestPersistenceContext.getInstance().get().getTransaction().setRollbackOnly();
            throw ex;
        } finally {

            if (method.getDeclaringClass() != Object.class) {
                bean.manager.popTransaction();
            }
        }
        return result;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invokeStatic(bean, proxy, method, args);
    }
}
