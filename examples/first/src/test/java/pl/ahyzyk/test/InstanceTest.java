package pl.ahyzyk.test;

import ejb.EasyEjb1;
import ejb.EasyEjb2;
import ejb.EasyEjb3;
import ejb.IEasyEjb3;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.ahyzyk.beanUnit.TestRunner;
import pl.ahyzyk.beanUnit.annotations.BeanImplementations;
import pl.ahyzyk.beanUnit.annotations.TestConfiguration;
import pl.ahyzyk.beanUnit.internal.BeanManager;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahyzyk on 30.04.2017.
 */
@RunWith(TestRunner.class)
@TestConfiguration(persistenceUnitName = "H2-eclipse")
public class InstanceTest {
    @Inject
    private EasyEjb2 easyEjb2;
    private EasyEjb1 easyEjb1;

    @Inject
    private Instance<EasyEjb2> instanes;

    @BeanImplementations
    public void implement(BeanManager beanManager) {
        beanManager.addImplementation(IEasyEjb3.class, EasyEjb3.class);
    }

    @Test
    public void test() {
        List<EasyEjb2> ejbs = new ArrayList<>();
        for (EasyEjb2 instane : instanes) {
            ejbs.add(instane);
        }
        Assert.assertEquals("Schould be 2 instacnes", 2, ejbs.size());
    }
}
