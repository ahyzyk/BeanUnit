package pl.ahyzyk.test;

import pl.ahyzyk.beanUnit.ConnectionHelper;


public class EclipseConnection extends ConnectionHelper {
    public String getPersistanceUnitName() {
        return "H2-eclipse";
    }
}
