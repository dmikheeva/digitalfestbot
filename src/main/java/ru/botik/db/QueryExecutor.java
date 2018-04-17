package ru.botik.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * Created by Daria on 13.04.2018.
 */
public class QueryExecutor {
    private static final Logger logger = LogManager.getLogger(QueryExecutor.class.getName());

    public static <V> V execCallable(String query, ICallableStatementHandler<V> handler) {
        Connection conn = null;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Start to execute callable statement: " + query);
            }
            conn = DbPoolConnection.getConnection();
            CallableStatement s = conn.prepareCall(query);
            handler.setParams(s);
            logger.debug("Params set to callable statement!");
            s.execute();
            V result = handler.getResult(s);
            logger.debug("Callable statement handles result!");
            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            DbPoolConnection.closeConnection(conn);
        }
        return null;
    }

    public static <V> V execQuery(String query, IPreparedStatementHandler<V> handler) {
        Connection conn = null;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Start to execute query: " + query);
            }
            conn = DbPoolConnection.getConnection();
            PreparedStatement s = conn.prepareStatement(query);
            handler.setParams(s);
            logger.debug("Params set to query statement!");
            ResultSet resultSet = s.executeQuery();
            V result = handler.getResult(resultSet);
            logger.debug("Query statement handles result!");
            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            DbPoolConnection.closeConnection(conn);
        }
        return null;
    }

    public static int execUpdate(String query, IPreparedStatementHandler handler) {
        Connection conn = null;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Start to execute update query: " + query);
            }
            conn = DbPoolConnection.getConnection();
            PreparedStatement s = conn.prepareStatement(query);
            handler.setParams(s);
            logger.debug("Params set to update query statement!");
            return s.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            DbPoolConnection.closeConnection(conn);
        }
        return -1;
    }

}
