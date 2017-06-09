package pl.ahyzyk.beanUnit.producers;

import pl.ahyzyk.beanUnit.internal.TestPersistenceContext;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.sql.DataSource;

/**
 * Created by ahyzyk on 09.06.2017.
 */
public class DataSourceProducer {
    @Produces
    public DataSource createDataSource(InjectionPoint ip) {
        return TestPersistenceContext.getInstance().getDataSource(TestPersistenceContext.getInstance().getDefault());
    }
}

