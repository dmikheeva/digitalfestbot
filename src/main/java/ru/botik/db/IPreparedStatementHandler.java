package ru.botik.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Daria on 15.04.2018.
 */
public interface IPreparedStatementHandler<V> extends IStatementParametersSetter<PreparedStatement> {
    V getResult(ResultSet rs) throws SQLException;
}
