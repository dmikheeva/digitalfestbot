package ru.botik.schedule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.botik.model.ActivityType;
import ru.botik.model.FestActivity;
import ru.botik.model.FestCourse;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import static ru.botik.schedule.ScheduleConstants.*;

/**
 * Создание расписания с учетом некоторых особенностей входного гуглдока, н-р: в первой строке содержится зоголовок,
 * который не читается; события "Открытие" и "Закрытие" учитываются по 1 разу; возраст может быть задан в формате
 * диапазона(5-10 или 5+) и тд (см кокретные реализации методов)
 *
 * Информацию о расписании не храним в базе
 * Создаем распсание по гуглдоку, присваивая "самописные" id-шники кажому пункту расписания
 */
public class GoogleSheetsScheduleUtils {
    private static final Logger logger = LogManager.getLogger(GoogleSheetsScheduleService.class.getName());

    public static List<FestActivity> createSectionActivities(List<List<Object>> scheduleValues,
                                                             int sectionNum,
                                                             boolean isParentEvent) throws IOException {
        int offset = sectionNum * SECTION_SIZE;
        String name = (String) scheduleValues.get(0).get(offset);//todo regular expr () Pattern.compile("\\((.*?)\\)");
        List<FestActivity> activities = new ArrayList<>();
        //пропускаем первую строку - там заголовок
        int activityId = sectionNum * 20;
        for (int i = 2; i < scheduleValues.size(); i++, activityId++) {
            if (scheduleValues.get(i).get(offset) != null) {
                FestActivity activity = createActivity(scheduleValues.get(i), sectionNum, activityId, name, isParentEvent);
                if (activity.getName() != null && !activity.getName().isEmpty()) {
                    if (closingEventStr.toLowerCase().equals(activity.getName().toLowerCase())) {
                        FestActivity parentActivity = new FestActivity(
                                activity.getId(), activity.getStartTime(), activity.getEndTime(),
                                activity.getName(), activity.getTutor(), ActivityType.FOR_PARENTS, activity.getRoom(),
                                activity.getStartAge(), activity.getEndAge(), activity.getSecret());
                        activities.add(parentActivity);
                    } else if (openEventStr.toLowerCase().equals(activity.getName().toLowerCase())) {
                        continue;
                    }
                    activities.add(activity);
                }
            }
        }
        return activities;
    }

    public static FestActivity createActivity(List<Object> row,
                                              int sectionNum,
                                              int activityId,
                                              String room,
                                              boolean isParentEvent) {
        int offset = sectionNum * SECTION_SIZE;
        Date startDate, endDate;
        int startAge = 0, endAge = 18;
        String name = "", tutor = "", secret = "";
        List<Date> dates;
        int[] ages;
        switch (row.size() - offset) {
            case 5:
                if (!isParentEvent) {
                    secret = (String) row.get(offset + 4);
                }
            case 4:
                if (isParentEvent) {
                    secret = (String) row.get(offset + 3);
                } else {
                    tutor = (String) row.get(offset + 3);
                }
            case 3:
                if (isParentEvent) {
                    tutor = (String) row.get(offset + 2);
                } else {
                    ages = parseAgeStr((String) row.get(offset + 2));
                    startAge = ages[0];
                    endAge = ages[1];
                }
            case 2:
                name = (String) row.get(offset + 1);
            case 1:
                dates = parseDateStr((String) row.get(offset));
                startDate = dates.get(0);
                endDate = dates.get(1);
                break;
            default:
                secret = (String) row.get(offset + (isParentEvent ? 3 : 4));
                tutor = (String) row.get(offset + (isParentEvent ? 2 : 3));
                if (!isParentEvent) {
                    ages = parseAgeStr((String) row.get(offset + 2));
                    startAge = ages[0];
                    endAge = ages[1];
                }
                name = (String) row.get(offset + 1);
                dates = parseDateStr((String) row.get(offset));
                startDate = dates.get(0);
                endDate = dates.get(1);
                break;
        }
        return new FestActivity(activityId, startDate, endDate, name,
                tutor, isParentEvent ? ActivityType.FOR_PARENTS : ActivityType.LECTURE, parseRoom(room),
                startAge, endAge, secret);
    }

    public static String parseRoom(String room) {
        //кабинеты в гуглдоке в формате Кабинет 4(синий)
        String roomName = room.substring(room.indexOf("(") + 1, room.indexOf(")"));
        return "'" + roomName.substring(0, 1).toUpperCase() + roomName.substring(1) + "'";
    }

    public static List<Date> parseDateStr(String dateStr) {
        List<Date> result = new ArrayList<>();
        result.add(null);
        result.add(null);
        dateStr = dateStr.replaceAll("\\s", "");//удаляем все пробелы
        Matcher m = timePattern.matcher(dateStr);
        if (m.find()) {
            try {
                if (m.group(1) != null) {
                    result.set(0, timeFormatter.parse(m.group(1)));
                }
                if (m.group(2) != null) {
                    result.set(1, timeFormatter.parse(m.group(2)));
                }
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return result;
    }

    public static int[] parseAgeStr(String dateStr) {
        int[] result = new int[2];
        result[0] = 0;
        result[1] = 100;
        dateStr = dateStr.replaceAll("\\s", "");//удаляем все пробелы
        Matcher m = agePatternRange.matcher(dateStr);
        if (m.find()) {
            if (m.group(1) != null) {
                result[0] = Integer.parseInt(m.group(1));
            }
            if (m.group(2) != null) {
                result[1] = Integer.parseInt(m.group(2));
            }
        } else {
            m = agePatternSinglePlus.matcher(dateStr);
            if (m.find()) {
                if (m.group(1) != null) {
                    result[0] = Integer.parseInt(m.group(1));
                }
                result[1] = 100;
            }
        }
        return result;
    }

    public static FestActivity createGameActivity(int activityId, List<Object> row) {
        int[] ages;
        int startAge = 0, endAge = 100;
        String name = "", room = "", secretWord = "";
        switch (row.size()) {
            case 4:
                secretWord = (String) row.get(3);
            case 3:
                ages = parseAgeStr((String) row.get(2));
                startAge = ages[0];
                endAge = ages[1];
            case 2:
                name = (String) row.get(1);
            case 1:
                room = (String) row.get(0);
                break;
            default:
                break;
        }
        return new FestActivity(activityId, null, null, name, null,
                ActivityType.GAME, room, startAge, endAge, secretWord);
    }

    /**
     * determines if time is suitable for showing in schedule
     *
     * @param activityTime
     * @return true if activityTime >= now-20min
     */
    public static boolean isSuitableTime(Date activityTime, Date now) {
        Calendar activityCalendar = Calendar.getInstance();
        activityCalendar.setTime(activityTime);
        Calendar today = Calendar.getInstance();
        activityCalendar.set(Calendar.YEAR, today.get(Calendar.YEAR));
        activityCalendar.set(Calendar.MONTH, today.get(Calendar.MONTH));
        activityCalendar.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
        activityCalendar.add(Calendar.MINUTE, 20);
        Date aTime = activityCalendar.getTime();
        return now.equals(aTime) || aTime.after(now);
    }

    public static FestCourse createCourse(int id, List<Object> row) {
        int[] ages;
        int startAge = 0, endAge = 100;
        String name = "", description = "";
        switch (row.size()) {
            case 3:
                description = (String) row.get(2);
            case 2:
                name = (String) row.get(1);
            case 1:
                ages = parseAgeStr((String) row.get(0));
                startAge = ages[0];
                endAge = ages[1];
                break;
            default:
                break;
        }
        return new FestCourse(id, startAge, endAge, name, description);

    }
}
