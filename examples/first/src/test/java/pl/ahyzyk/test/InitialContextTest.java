package pl.ahyzyk.test;

import org.junit.Test;
import pl.ahyzyk.beanUnit.dataSource.TestContextFactory;
import pl.ahyzyk.beanUnit.dataSource.TestDataSource;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

/**
 * Created by ahyzyk on 21.06.2017.
 */
public class InitialContextTest {
    @Test
    public void test() throws NamingException, ClassNotFoundException, SQLException {
        TestDataSource ds = new TestDataSource("org.h2.Driver",
                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                "", "");
        TestContextFactory.createTestContext()
                .add("test", "testCtxValue")
                .addDataSource("jdbc/H2", "org.h2.Driver",
                        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                        "", ""
                )
                .add("jdbc/H_2", ds);
        InitialContext ic = new InitialContext();
        assertEquals("testCtxValue", ic.lookup("test"));
        assertEquals("PUBLIC", ((DataSource) ic.lookup("jdbc/H_2")).getConnection().getSchema());
        assertEquals("PUBLIC", ((DataSource) ic.lookup("jdbc/H2")).getConnection().getSchema());

        TestDataSource.cleanUp();
    }
}
