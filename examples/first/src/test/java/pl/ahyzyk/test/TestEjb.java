package pl.ahyzyk.test;


import ejb.EasyEjb;
import ejb.Table1Manager;
import org.junit.Ignore;
import org.junit.Test;

import javax.ejb.EJB;
import javax.inject.Inject;


@Ignore
public class TestEjb {
    @EJB
    private EasyEjb easyEjb;

    @Inject
    private Integer integer;

    @EJB
    private Table1Manager table1Manager;

    @Test
    public void firstTest() {
        easyEjb.testMe();
        table1Manager.show();
    }

    @Test
    public void secoundTest() {
        easyEjb.testMe();
        table1Manager.show();
    }

}
