package pl.ahyzyk.test;


import ejb.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ahyzyk.beanUnit.annotations.*;
import pl.ahyzyk.beanUnit.internal.BeanManager;

import javax.ejb.EJB;
import javax.inject.Inject;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@Ignore
public class TestEjb {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestEjb.class);
    @EJB
    private EasyEjb easyEjb;

    @Inject
    private Integer integer;

    @EJB
    private SingletonEjb singletonEjb;

    @EJB
    private Table1Manager table1Manager;

    @EJB
    private BeanManager beanManager;

    @Before
    public void before() {
        LOGGER.info("Before");
    }

    @After
    public void after() {
        LOGGER.info("After");
    }

    @AfterDBUnit
    public void afterDbUnit() {
        LOGGER.info("afterDbUnit");
    }

    @BeforeDBUnit
    public void beforeDBUnit() {
        LOGGER.info("beforeDBUnit");
    }

    @BeanImplementations
    public void implement(BeanManager beanManager) {
        beanManager.addImplementation(IEasyEjb3.class, EasyEjb3.class);
    }


    @Test
    @ClearTable({"Table2", "Table1"})
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

    @Test
    @UsingDataSet("datasets/testEjb/singleColumnTableTest.xml")
    @ShouldMatchDataSet(value = "datasets/testEjb/singleColumnTableTest.xml", ordered = true)
    public void singleColumnTableTest() {
        //ok import/export should be the same
    }

}
