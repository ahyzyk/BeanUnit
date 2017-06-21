package pl.ahyzyk.beanUnit.dataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TestDataSource implements DataSource, Serializable {
    private static List<ConnectionKeeper> connectionList;
    private String connectionString;
    private String username;
    private String password;

    public TestDataSource(String driver, String connectionString, String username, String password) throws ClassNotFoundException {
        Class.forName(driver);
        this.connectionString = connectionString;
        this.username = username;
        this.password = password;
        connectionList = new ArrayList<>();
    }

    public static void cleanUp() {
        long openCount = connectionList.stream().filter(c -> {
            try {
                if (!c.connection.isClosed()) {
                    c.connection.close();
                    System.out.println("Not closed connection : " + c.stackTraceElement.toString());
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }).count();

        if (openCount > 0) {
            System.out.println("Connections should be closed");
        }
        connectionList = new ArrayList<>();
    }

    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(connectionString, username, password);
        connectionList.add(new ConnectionKeeper(conn, Thread.currentThread().getStackTrace()[2]));
        return conn;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public void setLoginTimeout(int seconds) throws SQLException {
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    private static class ConnectionKeeper {
        Connection connection;
        StackTraceElement stackTraceElement;

        public ConnectionKeeper(Connection connection, StackTraceElement stackTraceElement) {
            this.connection = connection;
            this.stackTraceElement = stackTraceElement;
        }
    }
}