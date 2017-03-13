package pl.ahyzyk.test;

import org.junit.runner.RunWith;
import pl.ahyzyk.beanUnit.TestConfiguration;
import pl.ahyzyk.beanUnit.TestRunner;


@RunWith(TestRunner.class)
@TestConfiguration(connectionHelper = EclipseConnection.class)
public class TestEclipse extends TestEjb {
}
