package pl.ahyzyk.test;


import ejb.EasyEjb;
import ejb.EasyEjb3;
import ejb.IEasyEjb3;
import ejb.Table1Manager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pl.ahyzyk.beanUnit.annotations.*;
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

    @Before
    public void before() {
        System.out.println("Before");
    }

    @After
    public void after() {
        System.out.println("After");
    }

    @AfterDBTest
    public void afterWithDB() {
        System.out.println("afterWithDB");
    }




    @BeanImplementations
    public void implement(BeanManager beanManager) {
        beanManager.addImplementation(IEasyEjb3.class, EasyEjb3.class);
    }


    @Test
    @ClearTable("Table1")
    @UsingDataSet("datasets/testEjb/test1.xml")
    @ShouldMatchDataSet("datasets/testEjb/test1.xml")
    public void test1ExpectedError() {
        //error database changed by bean
        easyEjb.testMe();
        table1Manager.show();
    }

    @Test
    @UsingDataSet("datasets/testEjb/test1.xml")
    @ShouldMatchDataSet("datasets/testEjb/test1_result.xml")
    @ClearTable("Table1")
    public void test2NotSortedExpectedError() {
        //error result not sorted
        easyEjb.testMe();
        table1Manager.show();
    }

    @Test
    @UsingDataSet("datasets/testEjb/test1.xml")
    @ShouldMatchDataSet(value = "datasets/testEjb/test1_result.xml", ordered = true)
    @ClearTable("Table1")
    public void test2SortedExpectedValid() {
        //ok result sorted
        easyEjb.testMe();
        table1Manager.show();
    }


    @Test
    @UsingDataSet("datasets/testEjb/test1.xml")
    @ShouldMatchDataSet(value = "datasets/testEjb/test1.xml", ordered = true)
    public void test3ExpectedValid() {
        //ok import/export should be the same
    }

    @Test
    @ClearTable("Table1")
    public void test4ExpectedValid() {
        //execute bean with mockito test on bean
        easyEjb.testMe();
        table1Manager.show();
        verify(beanManager.getSpy(EasyEjb.class), times(1)).testMe();
    }

}
