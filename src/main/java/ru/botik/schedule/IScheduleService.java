package ru.botik.schedule;

import ru.botik.model.FestActivity;
import ru.botik.model.FestCourse;

import java.io.IOException;
import java.util.List;

/**
 * Created by Daria on 14.04.2018.
 */
public interface IScheduleService {
    List<FestActivity> createLectureSchedule() throws IOException;

    List<FestActivity> createGameSchedule() throws IOException;

    List<FestCourse> createCourses() throws IOException;

    boolean fillSchedules();

    List<FestActivity> getFestLecturesSchedule();

    List<FestActivity> getFestGameSchedule();

    List<FestCourse> getFestCourses();
}
