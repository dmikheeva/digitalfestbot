package ru.botik.db;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Daria on 15.04.2018.
 */
public interface IStatementParametersSetter<T extends Statement> {
    void setParams(T statement) throws SQLException;
}
