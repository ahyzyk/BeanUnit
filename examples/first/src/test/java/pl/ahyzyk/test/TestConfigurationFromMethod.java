package pl.ahyzyk.test;

import org.junit.runner.RunWith;
import pl.ahyzyk.beanUnit.TestRunner;
import pl.ahyzyk.beanUnit.annotations.TestConfiguration;

import java.lang.annotation.Annotation;

@RunWith(TestRunner.class)
public class TestConfigurationFromMethod extends TestEjb {
    @TestConfiguration
    public static TestConfiguration configure() {
        return new TestConfiguration() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String persistenceUnitName() {
                return "H2-eclipse";
            }
        };
    }
}
