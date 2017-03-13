package pl.ahyzyk.test;

import org.junit.runner.RunWith;
import pl.ahyzyk.beanUnit.TestConfiguration;
import pl.ahyzyk.beanUnit.TestRunner;


@RunWith(TestRunner.class)
@TestConfiguration(connectionHelper = HibernateConnection.class)
public class TestHibernate extends TestEjb {
}
