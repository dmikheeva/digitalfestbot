package ru.botik.schedule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.botik.model.ActivityType;
import ru.botik.model.FestActivity;
import ru.botik.model.FestCourse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static ru.botik.schedule.ScheduleConstants.*;

/**
 * Created by Daria on 08.11.2017.
 */
public class GoogleSheetsScheduleService implements IScheduleService {
    private static final Logger logger = LogManager.getLogger(GoogleSheetsScheduleService.class.getName());
    private SpreadsheetConnection spreadsheetConnection;

    private List<FestActivity> festLecturesSchedule;
    private List<FestActivity> festGameSchedule;
    private List<FestCourse> festCourses;

    public GoogleSheetsScheduleService() {
        init();
    }

    private void init() {
        Properties properties = new Properties();
        String spreadsheetId = "", keyFileName = "";
        try (FileInputStream in = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(in);
            spreadsheetId = properties.getProperty("google.spreadsheet.id");
            keyFileName = properties.getProperty("google.keyFile");
        } catch (IOException e) {
            e.printStackTrace();
        }
        spreadsheetConnection = new SpreadsheetConnection(spreadsheetId, keyFileName);
        fillSchedules();
    }

    @Override
    public boolean fillSchedules() {
        try {
            logger.debug("Start updating schedule");
            festLecturesSchedule = createLectureSchedule();
            festGameSchedule = createGameSchedule();
            festCourses = createCourses();
            logger.debug("All schedules updated successfully!");
            return true;
        } catch (IOException e) {
            logger.error(e);
        }
        return false;
    }

    public List<FestActivity> createLectureSchedule() throws IOException {
        List<List<Object>> values = spreadsheetConnection.getDataRange(lectureScheduleStr).getValues();
        if (values == null || values.isEmpty()) {
            logger.warn("No lectures data found.");
            return null;
        }
        int sectionsNum = values.get(0).size() / SECTION_SIZE + (values.get(0).size() % SECTION_SIZE == 0 ? 0 : 1);
        List<FestActivity> activities = new ArrayList<>();
        //
        for (int i = 0; i < sectionsNum; i++) {
            activities.addAll(GoogleSheetsScheduleUtils.createSectionActivities(values, i, i == (sectionsNum - 1)));
        }
        logger.debug("Lecture schedule updated successfully!");
        return activities;
    }

    public List<FestActivity> createGameSchedule() throws IOException {
        List<List<Object>> values = spreadsheetConnection.getDataRange(allDayActivitiesStr).getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No games data found.");
            return null;
        }
        List<FestActivity> activities = new ArrayList<>();
        for (int i = 1, activityId = 1000; i < values.size(); i++, activityId++) {
            FestActivity gameActivity = GoogleSheetsScheduleUtils.createGameActivity(activityId, values.get(i));
            if (gameActivity.getName() != null && !gameActivity.getName().isEmpty()) {
                activities.add(GoogleSheetsScheduleUtils.createGameActivity(activityId, values.get(i)));
            }
        }
        logger.debug("Activity schedule updated successfully!");
        return activities;
    }

    public List<FestCourse> createCourses() throws IOException {
        List<List<Object>> values = spreadsheetConnection.getDataRange(offerCourcesStr).getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No fest courses data found.");
            return null;
        }
        List<FestCourse> courses = new ArrayList<>();
        for (int i = 1; i < values.size(); i++) {
            courses.add(GoogleSheetsScheduleUtils.createCourse(i, values.get(i)));
        }
        logger.debug("Course schedule updated successfully!");
        return courses;
    }

    public static List<FestActivity> findSuitableActivities(List<FestActivity> allActivities, int age) {
        List<FestActivity> suitable;
        Date now = new Date();
        if (age != 50) {
            suitable = allActivities.stream().
                    filter(a -> a.getStartAge() <= age && a.getEndAge() >= age &&
                            ActivityType.LECTURE.equals(a.getType()) &&
                            GoogleSheetsScheduleUtils.isSuitableTime(a.getStartTime(), now)).
                    collect(Collectors.toList());
        } else {
            suitable = allActivities.stream().
                    filter(a -> ActivityType.FOR_PARENTS.equals(a.getType()) &&
                            GoogleSheetsScheduleUtils.isSuitableTime(a.getStartTime(), now)).
                    collect(Collectors.toList());
        }
        if (suitable.size() == 0) {
            return suitable;
        }
        suitable.sort((FestActivity a1, FestActivity a2) -> a1.getStartTime().compareTo(a2.getStartTime()));
        FestActivity earliestActivity = suitable.get(0);
        suitable = suitable.stream()
                .filter(a -> a.getStartTime().equals(earliestActivity.getStartTime()))
                .collect(Collectors.toList());
        return suitable;
    }

    @Override
    public List<FestActivity> getFestLecturesSchedule() {
        return festLecturesSchedule;
    }

    @Override
    public List<FestActivity> getFestGameSchedule() {
        return festGameSchedule;
    }

    @Override
    public List<FestCourse> getFestCourses() {
        return festCourses;
    }


}
