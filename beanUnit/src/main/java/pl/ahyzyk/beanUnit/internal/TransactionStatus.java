package pl.ahyzyk.beanUnit.internal;

/**
 * Created by ahyzyk on 14.04.2017.
 */
public enum TransactionStatus {
    ACTIVE(true),
    CREATE_NEW(true),
    NONE(false),
    TO_NONE(false);
    private final boolean transaction;

    TransactionStatus(Boolean transaction) {
        this.transaction = transaction;
    }

    public boolean isTransaction() {
        return transaction;
    }
}
