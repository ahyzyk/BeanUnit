package pl.ahyzyk.test;


import ejb.EasyEjb;
import ejb.Table1Manager;
import org.junit.Ignore;
import org.junit.Test;
import pl.ahyzyk.beanUnit.internal.BeanManager;

import javax.ejb.EJB;
import javax.inject.Inject;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@Ignore
public class TestEjb {
    @EJB
    private EasyEjb easyEjb;

    @Inject
    private Integer integer;

    @EJB
    private Table1Manager table1Manager;

    @EJB
    private BeanManager beanManager;

    @Test
    public void test1() {
        easyEjb.testMe();
        table1Manager.show();
    }

    @Test
    public void test2() {
        easyEjb.testMe();
        table1Manager.show();
    }

    @Test
    public void test3() {
        easyEjb.testMe();
        table1Manager.show();
    }

    @Test
    public void test4() {

        easyEjb.testMe();
        table1Manager.show();

        verify(beanManager.getSpy(EasyEjb.class), times(1)).testMe();
    }

}
