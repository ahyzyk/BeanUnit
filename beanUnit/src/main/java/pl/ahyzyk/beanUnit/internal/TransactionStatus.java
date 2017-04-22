package pl.ahyzyk.beanUnit.internal;

/**
 * Created by ahyzyk on 14.04.2017.
 */
public enum TransactionStatus {
    ACTIVE(true, false),
    CREATE_NEW(true, true),
    NONE(false, false),
    TO_NONE(false, true);
    private final boolean transactional;
    private final boolean createNewEntityManager;

    TransactionStatus(boolean transactional, boolean createNewEntityManager) {
        this.transactional = transactional;
        this.createNewEntityManager = createNewEntityManager;
    }

    public boolean isTransactional() {
        return transactional;
    }

    public boolean isCreateNewEntityManager() {
        return createNewEntityManager;
    }
}
