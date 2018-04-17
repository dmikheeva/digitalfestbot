package ru.botik.db;

import ru.botik.model.UserActionType;

import java.util.List;

/**
 * Created by Daria on 13.04.2018.
 */
public interface IDBService {
    void createUser(long id, boolean isChild, String userName);

    int setCorrectWord(long userId, int activityId, String word);

    int setUserAction(long id, UserActionType action, Integer activityId);

    int setUserAction(long id, UserActionType action);

    UserActionType getUserAction(long id);

    int getUserActionActivityId(long id);

    boolean isWordGuessed(long id, int activityId);

    int getPoints(long id);

    List<Long> getChatIds();

    void setChildAge(long id, int age);

    int getAge(long id);
}
