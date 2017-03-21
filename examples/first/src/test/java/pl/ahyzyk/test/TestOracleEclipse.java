package pl.ahyzyk.test;

import org.junit.runner.RunWith;
import pl.ahyzyk.beanUnit.TestRunner;
import pl.ahyzyk.beanUnit.annotations.TestConfiguration;


@RunWith(TestRunner.class)
@TestConfiguration(persistenceUnitName = "Oracle-eclipse")
public class TestOracleEclipse extends TestEjb {
}
