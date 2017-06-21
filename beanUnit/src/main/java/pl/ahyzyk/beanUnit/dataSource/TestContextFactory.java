package pl.ahyzyk.beanUnit.dataSource;

import javax.naming.spi.NamingManager;

public class TestContextFactory {

    private static TestContext ctx = null;

    /**
     * do not instantiate this class directly. Use the factory method.
     */
    private TestContextFactory() {
    }

    public static TestContext createTestContext() {
        if (ctx != null) {
            return ctx;
        }
        try {
            TestContext localCtx = new TestContext();
            NamingManager.setInitialContextFactoryBuilder(localCtx);
            ctx = localCtx;
            return ctx;
        } catch (Exception e) {
            throw new RuntimeException("Error Initializing Context: " + e.getMessage(), e);
        }
    }
}