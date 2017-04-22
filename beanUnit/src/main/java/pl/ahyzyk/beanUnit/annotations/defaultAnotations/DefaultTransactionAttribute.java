package pl.ahyzyk.beanUnit.annotations.defaultAnotations;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * Created by ahyzyk on 22.04.2017.
 */
public class DefaultTransactionAttribute implements TransactionAttribute {
    private static DefaultTransactionAttribute instanceEjb = new DefaultTransactionAttribute(TransactionAttributeType.REQUIRED);
    private static DefaultTransactionAttribute instanceView = new DefaultTransactionAttribute(TransactionAttributeType.NEVER);

    private TransactionAttributeType type;

    private DefaultTransactionAttribute(TransactionAttributeType type) {
        this.type = type;
    }

    public static DefaultTransactionAttribute getInstance(Class beanClass) {
        long count = Arrays.stream(beanClass.getDeclaredAnnotations())
                .filter(c -> c.annotationType().getName().startsWith("javax.ejb."))
                .count();
        if (count > 0) {
            return instanceEjb;
        } else {
            return instanceView;
        }
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    @Override
    public TransactionAttributeType value() {
        return type;
    }
}
