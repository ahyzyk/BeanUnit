package pl.ahyzyk.beanUnit.dataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class TestContext extends InitialContext implements InitialContextFactoryBuilder, InitialContextFactory {


    private Map<String, Object> contextMap;

    public TestContext() throws NamingException {
        super();
        contextMap = new HashMap<>();
    }

    public TestContext addDataSource(String name, String driver, String connectionString, String username, String password) {
        try {
            this.contextMap.put(name, new TestDataSource(driver, connectionString, username, password));
            return this;
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Error during create datasource", ex);
        }
    }

    public TestContext add(String name, Object object) {
        this.contextMap.put(name, object);
        return this;
    }


    public Context getInitialContext(Hashtable<?, ?> arg0) throws NamingException {
        return this;
    }

    @Override
    public Object lookup(String name) throws NamingException {
        if (name == null) {
            return null;
        }

        Object ret = contextMap.get(name);
        return (ret != null) ? ret : super.lookup(name);
    }

    @Override
    public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) throws NamingException {
        contextMap.putAll((Map<? extends String, ?>) environment);
        return this;
    }



}