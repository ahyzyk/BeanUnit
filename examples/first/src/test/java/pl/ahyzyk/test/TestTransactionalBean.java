package pl.ahyzyk.test;

import ejb.Table1Manager;
import ejb.TransactionalBean;
import org.hibernate.LazyInitializationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.ahyzyk.beanUnit.TestRunner;
import pl.ahyzyk.beanUnit.annotations.ClearTable;
import pl.ahyzyk.beanUnit.annotations.TestConfiguration;
import pl.ahyzyk.beanUnit.annotations.UsingDataSet;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * Created by ahyzyk on 14.04.2017.
 */
@RunWith(TestRunner.class)
@TestConfiguration(persistenceUnitName = "H2-hibernate")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TestTransactionalBean {
    @EJB
    TransactionalBean bean;
    @EJB
    Table1Manager table1Manager;

    @Test
    public void neverTest() {
        bean.never();
    }

    @Test(expected = RuntimeException.class)
    public void neverErrorTest() {
        bean.neverError();
    }

    @Test
    public void neverOkTest() {
        bean.neverOk();
    }

    @Test(expected = RuntimeException.class)
    public void mandatoryTest() {
        bean.mandatory();
    }

    @Test(expected = RuntimeException.class)
    public void mandatoryErrorTest() {
        bean.mandatoryError();
    }

    @Test
    public void mandatoryOkTest() {
        bean.mandatoryOk();
    }

    @Test(expected = LazyInitializationException.class)
    @UsingDataSet("datasets/testEjb/table1join.xml")
    @ClearTable({"TABLE2", "TABLE1"})
    public void deatachedTest() {
        table1Manager.get(1L).getListData().isEmpty();
    }

    @Test
    @UsingDataSet("datasets/testEjb/table1join.xml")
    @ClearTable({"TABLE2", "TABLE1"})
    public void deatached2Test() {
        table1Manager.get2(1L).getListData().isEmpty();
    }

    @Test
    @UsingDataSet("datasets/testEjb/table1join.xml")
    @ClearTable({"TABLE2", "TABLE1"})
    public void deatached3Test() {
        table1Manager.get3(1L).getListData().isEmpty();
    }

    @Test(expected = RuntimeException.class)
    @UsingDataSet("datasets/testEjb/table1join.xml")
    @ClearTable({"TABLE2", "TABLE1"})
    public void deatached4Test() {
        table1Manager.get4(1L);
    }
}
