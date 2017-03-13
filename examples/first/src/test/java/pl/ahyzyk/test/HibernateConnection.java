package pl.ahyzyk.test;

import pl.ahyzyk.beanUnit.ConnectionHelper;


public class HibernateConnection extends ConnectionHelper {

    public String getPersistanceUnitName() {
        return "H2-hibernate";
    }
}
