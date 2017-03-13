package pl.ahyzyk.beanUnit.internal;

import pl.ahyzyk.beanUnit.ConnectionHelper;

public class EmptyConnectionHelper extends ConnectionHelper {

    @Override
    public String getPersistanceUnitName() {
        return null;
    }
}
