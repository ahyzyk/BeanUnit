package pl.ahyzyk.test;

import org.junit.runner.RunWith;
import pl.ahyzyk.beanUnit.TestRunner;
import pl.ahyzyk.beanUnit.annotations.TestConfiguration;


@RunWith(TestRunner.class)
@TestConfiguration(persistenceUnitName = "H2-eclipse")
public class TestH2Eclipse extends TestEjb {
}
