package ejb;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * Created by ahyzyk on 14.04.2017.
 */
@Stateless
public class TransactionalBean {
    @EJB
    TransactionalBean ejb;

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void never() {

    }

    public void neverError() {
        ejb.never();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void neverOk() {
        never();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void mandatoryError() {
        ejb.mandatory();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void mandatoryOk() {
        mandatory();
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void mandatory() {

    }


}
