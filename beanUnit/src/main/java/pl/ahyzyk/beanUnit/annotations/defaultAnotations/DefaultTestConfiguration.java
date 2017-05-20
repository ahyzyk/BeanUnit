package pl.ahyzyk.beanUnit.annotations.defaultAnotations;

import pl.ahyzyk.beanUnit.annotations.TestConfiguration;

import java.lang.annotation.Annotation;

/**
 * Created by ahyzyk on 20.05.2017.
 */
public class DefaultTestConfiguration implements TestConfiguration {
    @Override
    public String persistenceUnitName() {
        return "";
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
