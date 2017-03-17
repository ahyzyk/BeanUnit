package pl.ahyzyk.test;


import ejb.EasyEjb;
import ejb.Table1Manager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pl.ahyzyk.beanUnit.annotations.ClearTable;
import pl.ahyzyk.beanUnit.annotations.DataSetDirectory;
import pl.ahyzyk.beanUnit.annotations.UsingDataSet;
import pl.ahyzyk.beanUnit.internal.BeanManager;

import javax.ejb.EJB;
import javax.inject.Inject;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@Ignore
@DataSetDirectory("datasets/testEjb")
public class TestEjb {
    @EJB
    private EasyEjb easyEjb;

    @Inject
    private Integer integer;

    @EJB
    private Table1Manager table1Manager;

    @EJB
    private BeanManager beanManager;

    @Before
    public void before() {
        System.out.println("Before");
    }


    @After
    public void after() {
        System.out.println("After");
    }

    @Test
    @UsingDataSet
    @ClearTable("Table1")
    public void test1() {
        easyEjb.testMe();
        table1Manager.show();
    }

    @Test
    @ClearTable("Table1")
    public void test2() {
        easyEjb.testMe();
        table1Manager.show();
    }

    @Test
    @ClearTable("Table1")
    public void test3() {
        easyEjb.testMe();
        table1Manager.show();
    }

    @Test
    @ClearTable("Table1")
    public void test4() {

        easyEjb.testMe();
        table1Manager.show();

        verify(beanManager.getSpy(EasyEjb.class), times(1)).testMe();
    }

}
