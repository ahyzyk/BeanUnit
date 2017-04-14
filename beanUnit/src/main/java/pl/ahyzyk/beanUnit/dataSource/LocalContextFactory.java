package pl.ahyzyk.beanUnit.dataSource;

import javax.naming.spi.NamingManager;

public class LocalContextFactory {
    /**
     * do not instantiate this class directly. Use the factory method.
     */
    private LocalContextFactory() {
    }

    public static LocalContext createLocalContext(String databaseDriver) {

        try {
            LocalContext ctx = new LocalContext();
            Class.forName(databaseDriver);
            NamingManager.setInitialContextFactoryBuilder(ctx);
            return ctx;
        } catch (Exception e) {
            throw new RuntimeException("Error Initializing Context: " + e.getMessage(), e);
        }
    }
}