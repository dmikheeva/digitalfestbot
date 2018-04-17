package ru.botik.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

public class DbPoolConnection {
    private static final Logger logger = LogManager.getLogger(DbPoolConnection.class.getName());
    private static int MAX_TOTAL_CONNECTIONS = 10;
    private static int CONNECTIONS_INITIAL_SIZE = 10;
    private static int MAX_OPEN_PREPARED_STATEMENTS = 100;

    private static BasicDataSource connectionPool;

    public DbPoolConnection(String driverClassName,
                            String host,
                            String port,
                            String dbName,
                            String userName,
                            String password) throws URISyntaxException, SQLException {
        initPool(driverClassName, host, port, dbName, userName, password);
    }

    private void initPool(String driverClassName,
                          String host,
                          String port,
                          String dbName,
                          String userName,
                          String password) {
        connectionPool = new BasicDataSource();
        connectionPool.setUrl(String.format("jdbc:mysql://%s:%s/%s?useSSL=false", host, port, dbName));
        connectionPool.setUsername(userName);
        connectionPool.setPassword(password);
        connectionPool.setDriverClassName(driverClassName);//com.mysql.cj.jdbc.Driver
        //connectionPool.setMinIdle(10);
        //connectionPool.setMaxIdle(100);
        connectionPool.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connectionPool.setInitialSize(CONNECTIONS_INITIAL_SIZE);
        connectionPool.setMaxOpenPreparedStatements(MAX_OPEN_PREPARED_STATEMENTS);
    }

    public static BasicDataSource getConnectionPool() {
        return connectionPool;
    }

    public static Connection getConnection() {
        try {
            return connectionPool.getConnection();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
