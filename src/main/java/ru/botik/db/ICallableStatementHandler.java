package ru.botik.db;

import java.sql.CallableStatement;
import java.sql.SQLException;

/** выполнение Callable Statement-ов
 *
 * @param <V> тип возвращаемого значения из обработчика
 */
public interface ICallableStatementHandler<V> extends IStatementParametersSetter<CallableStatement> {
    V getResult(CallableStatement statement) throws SQLException;
}
