package pl.ahyzyk.beanUnit.annotations.defaultAnotations;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.lang.annotation.Annotation;

/**
 * Created by ahyzyk on 22.04.2017.
 */
public class DefaultTransactionManagement implements TransactionManagement {
    private static DefaultTransactionManagement instance = new DefaultTransactionManagement();

    private DefaultTransactionManagement() {

    }

    public static DefaultTransactionManagement getInstance() {
        return instance;
    }

    @Override
    public TransactionManagementType value() {
        return TransactionManagementType.CONTAINER;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
