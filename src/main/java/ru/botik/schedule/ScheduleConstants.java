package ru.botik.schedule;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * Created by Daria on 12.04.2018.
 */
public final class ScheduleConstants {
    private ScheduleConstants() {
    }

    public static final Pattern timePattern = Pattern.compile("(\\d*:\\d*)-(\\d*:\\d*)");
    //паттерн для диапазона возраста, например 5-10
    public static final Pattern agePatternRange = Pattern.compile("(\\d*)-(\\d*)");
    //паттерн для возрастов вида 5+
    public static final Pattern agePatternSinglePlus = Pattern.compile("(\\d*)\\+");
    public static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
    public static final int SECTION_SIZE = 5;

    public static final String lectureScheduleStr = "'Расписание лекций'";
    public static final String allDayActivitiesStr = "'Активности - весь день'";
    public static final String closingEventStr = "ЗАКРЫТИЕ";
    public static final String openEventStr = "ОТКРЫТИЕ";
    public static final String offerCourcesStr = "'Курсы в конце'";




}
