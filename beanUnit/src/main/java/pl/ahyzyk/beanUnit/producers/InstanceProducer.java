package pl.ahyzyk.beanUnit.producers;

import org.apache.poi.ss.formula.functions.T;
import pl.ahyzyk.beanUnit.beans.TestInstance;
import pl.ahyzyk.beanUnit.internal.BeanManager;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

/**
 * Created by ahyzyk on 29.04.2017.
 */
public class InstanceProducer {
    @Inject
    private BeanManager beanManager;

    @Produces
    public Instance<T> instanceProducer(InjectionPoint ip) {
        ip.getMember().getClass().getGenericInterfaces();
        return new TestInstance<>();
    }

}
