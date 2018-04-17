package ru.botik.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daria on 13.04.2018.
 */
public enum UserActionType {
    AGE_INPUT(1),//ожидание ввода возраста
    SCHEDULE_ACCESS(2),//работа с расписанием
    WORD_GUESSING(3),//угадывание контрольного слова
    FEST_EVALUATION_LIKE(4),//фестиваль понравился - работа с курсами
    FEST_EVALUATION_DISLIKE(5);//фестиваль не понравился

    private final int value;
    private static Map<Integer, UserActionType> map = new HashMap<>();

    static {
        for (UserActionType type : UserActionType.values()) {
            map.put(type.value, type);
        }
    }

    private UserActionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserActionType valueOf(int value) {
        return map.get(value);
    }
}
