package pl.ahyzyk.test;


import ejb.EasyEjb;
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

    @Test
    public void firstTest() {
        easyEjb.testMe();
    }

    @Test
    public void secoundTest() {
        easyEjb.testMe();
    }

}
