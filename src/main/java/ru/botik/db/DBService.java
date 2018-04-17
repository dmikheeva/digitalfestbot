package ru.botik.db;

import ru.botik.model.UserActionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс содержит все запросы к базе
 */
public class DBService implements IDBService {
    public void createUser(long id, boolean isChild, String userName) {
        QueryExecutor.execCallable("{call createUser(?, ?, ?, ?)}", new ICallableStatementHandler<Void>() {
            @Override
            public Void getResult(CallableStatement statement) throws SQLException {
                return null;
            }

            @Override
            public void setParams(CallableStatement st) throws SQLException {
                st.setLong(1, id);
                st.setBoolean(2, isChild);
                st.setString(3, userName);
                st.setInt(4, UserActionType.AGE_INPUT.getValue());
            }
        });
    }

    public void setChildAge(long id, int age) {
        QueryExecutor.execCallable("{call setChildAge(?, ?, ?)}", new ICallableStatementHandler<Void>() {
            @Override
            public void setParams(CallableStatement st) throws SQLException {
                st.setLong(1, id);
                st.setInt(2, age);
                st.setInt(3, UserActionType.SCHEDULE_ACCESS.getValue());
            }

            @Override
            public Void getResult(CallableStatement statement) throws SQLException {
                return null;
            }
        });
    }

    public int setCorrectWord(long userId, int activityId, String word) {
        Integer res = QueryExecutor.execCallable("{call setCorrectWord(?, ?, ?, ?, ?)}", new ICallableStatementHandler<Integer>() {
            @Override
            public void setParams(CallableStatement st) throws SQLException {
                st.registerOutParameter(1, Types.NUMERIC);
                st.setLong(2, userId);
                st.setInt(3, activityId);
                st.setString(4, word);
                st.setInt(5, UserActionType.SCHEDULE_ACCESS.getValue());
            }

            @Override
            public Integer getResult(CallableStatement statement) throws SQLException {
                return statement.getInt(1);
            }
        });
        return res != null ? res : -1;
    }

    public int setUserAction(long id, UserActionType action) {
        return setUserAction(id, action, null);
    }

    public int setUserAction(long id, UserActionType action, Integer activityId) {
        return QueryExecutor.execUpdate("UPDATE user_action set action_id = ?, activity_id = ? where user_id = ?",
                new IPreparedStatementHandler<Void>() {
                    @Override
                    public void setParams(PreparedStatement st) throws SQLException {
                        st.setInt(1, action.getValue());
                        st.setObject(2, activityId);
                        st.setLong(3, id);
                    }

                    @Override
                    public Void getResult(ResultSet rs) throws SQLException {
                        return null;
                    }

                });
    }

    public UserActionType getUserAction(long id) {
        return QueryExecutor.execQuery("SELECT action_id FROM user_action where user_id = ?",
                new IPreparedStatementHandler<UserActionType>() {
                    @Override
                    public void setParams(PreparedStatement st) throws SQLException {
                        st.setLong(1, id);
                    }

                    @Override
                    public UserActionType getResult(ResultSet rs) throws SQLException {
                        int res = -1;
                        while (rs.next()) {
                            res = rs.getInt("action_id");
                        }
                        return UserActionType.valueOf(res);
                    }
                });
    }


    public int getUserActionActivityId(long id) {
        Integer result = QueryExecutor.execQuery("SELECT activity_id FROM user_action where user_id = ?",
                new IPreparedStatementHandler<Integer>() {
                    @Override
                    public void setParams(PreparedStatement st) throws SQLException {
                        st.setLong(1, id);
                    }

                    @Override
                    public Integer getResult(ResultSet rs) throws SQLException {
                        int res = -1;
                        while (rs.next()) {
                            res = rs.getInt("activity_id");
                        }
                        return res;
                    }
                });
        return result != null ? result : -1;
    }


    public boolean isWordGuessed(long id, int activityId) {
        Boolean result = QueryExecutor.execQuery("SELECT uw.id id FROM user_words uw where user_id = ? and activity_id = ?",
                new IPreparedStatementHandler<Boolean>() {
                    @Override
                    public void setParams(PreparedStatement st) throws SQLException {
                        st.setLong(1, id);
                        st.setInt(2, activityId);
                    }

                    @Override
                    public Boolean getResult(ResultSet rs) throws SQLException {
                        int res = -1;
                        while (rs.next()) {
                            res = rs.getInt("id");
                        }
                        return res != -1;
                    }
                });
        return result != null ? result : false;
    }


    public int getPoints(long id) {
        Integer result = QueryExecutor.execQuery("SELECT count(*) t FROM user_words where user_id = ?",
                new IPreparedStatementHandler<Integer>() {
                    @Override
                    public void setParams(PreparedStatement st) throws SQLException {
                        st.setLong(1, id);
                    }

                    @Override
                    public Integer getResult(ResultSet rs) throws SQLException {
                        int res = -1;
                        while (rs.next()) {
                            res = rs.getInt("t");
                        }
                        return res;

                    }
                });
        return result != null ? result : -1;
    }


    public int getAge(long id) {
        Integer result = QueryExecutor.execQuery("SELECT age FROM users where id = ?",
                new IPreparedStatementHandler<Integer>() {
                    @Override
                    public void setParams(PreparedStatement st) throws SQLException {
                        st.setLong(1, id);
                    }

                    @Override
                    public Integer getResult(ResultSet rs) throws SQLException {
                        int res = -1;
                        while (rs.next()) {
                            res = rs.getInt("age");
                        }
                        return res;
                    }

                });
        return result != null ? result : -1;
    }

    public List<Long> getChatIds() {
        return QueryExecutor.execQuery("SELECT id FROM users", new IPreparedStatementHandler<List<Long>>() {
            @Override
            public void setParams(PreparedStatement st) throws SQLException {
            }

            @Override
            public List<Long> getResult(ResultSet rs) throws SQLException {
                List<Long> chatIds = new ArrayList<>();
                while (rs.next()) {
                    chatIds.add(rs.getLong("id"));
                }
                return chatIds;
            }
        });
    }
}
