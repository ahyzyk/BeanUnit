package pl.ahyzyk.beanUnit.producers;

import org.apache.poi.ss.formula.functions.T;
import pl.ahyzyk.beanUnit.beans.InstanceImpl;
import pl.ahyzyk.beanUnit.internal.BeanManager;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * Created by ahyzyk on 29.04.2017.
 */
public class InstanceProducer {
    @Inject
    private BeanManager beanManager;

    @Produces
    public Instance<T> instanceProducer(InjectionPoint ip) {
        ParameterizedType params = (ParameterizedType) ((Field) ip.getMember()).getGenericType();
        return new InstanceImpl<>((Class) params.getActualTypeArguments()[0]);
    }

}
