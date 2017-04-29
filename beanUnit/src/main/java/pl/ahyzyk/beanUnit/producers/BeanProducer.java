package pl.ahyzyk.beanUnit.producers;

import pl.ahyzyk.beanUnit.internal.BeanManagerContext;
import pl.ahyzyk.beanUnit.internal.TestBean;
import pl.ahyzyk.beanUnit.internal.beans.BeanContext;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Created by ahyzyk on 29.04.2017.
 */
public class BeanProducer {

    @Produces
    public Object defaultProducer(InjectionPoint ip) throws IllegalAccessException, InstantiationException {
        Class clazz = (Class) ip.getType();
        if (BeanManagerContext.get(clazz) == null) {
            Class implementation = BeanManagerContext.getImplementation(clazz);
            Object result = implementation.newInstance();
            TestBean bean = new TestBean(result, BeanContext.getTestBeanManager());
            BeanManagerContext.add(clazz, bean);
            BeanContext.fillBean(bean.getSpy(), bean.getBeanClass());
        }
        return BeanManagerContext.getCurrent().getBeans().get((Class) ip.getType()).getBean();
    }
}
