package pl.ahyzyk.beanUnit.producers;

import pl.ahyzyk.beanUnit.internal.TestEntityManager;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by ahyzyk on 29.04.2017.
 */
public class PersistenceContextProducer {
    @Produces
    public EntityManager createEntityManager(InjectionPoint ip) {
        if (ip.getAnnotated().isAnnotationPresent(PersistenceContext.class)) {
            PersistenceContext persistenceAnnotation = ip.getAnnotated().getAnnotation(PersistenceContext.class);
            return new TestEntityManager(persistenceAnnotation.unitName());
        }
        throw new RuntimeException("EntityManager without PersistenceContext annotation");
    }
}
