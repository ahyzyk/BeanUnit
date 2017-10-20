package pl.ahyzyk.test;

import ejb.EasyEjb3;
import ejb.IEasyEjb3;
import ejb.PrivateEjb;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.ahyzyk.beanUnit.TestRunner;
import pl.ahyzyk.beanUnit.annotations.BeanImplementations;
import pl.ahyzyk.beanUnit.internal.BeanManager;

import javax.ejb.EJB;

/**
 * Created by ahyzyk on 20.10.2017.
 */
@RunWith(TestRunner.class)
public class PrivateEjbTest {
    @EJB
    private PrivateEjb ejb;


    @BeanImplementations
    public void implement(BeanManager beanManager) {
        beanManager.addImplementation(IEasyEjb3.class, EasyEjb3.class);
    }

    @Test
    public void test() {
        ejb.method1();
        ejb.method3();
    }
}
