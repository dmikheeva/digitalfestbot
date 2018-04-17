package ru.botik.model;

import java.util.Date;

/**
 * Created by Daria on 08.11.2017.
 */
public class FestActivity {
    private int id;
    private Date startTime;
    private Date endTime;
    private String name;
    private String tutor;
    private ActivityType type;
    private String room;
    private int startAge;
    private int endAge;
    private String secret;

    public FestActivity(int id,
                        Date startTime,
                        Date endTime,
                        String name,
                        String tutor,
                        ActivityType type,
                        String room,
                        int startAge,
                        int endAge,
                        String secret) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.tutor = tutor;
        this.type = type;
        this.room = room;
        this.startAge = startAge;
        this.endAge = endAge;
        this.secret = secret;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTutor() {
        return tutor;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getStartAge() {
        return startAge;
    }

    public void setStartAge(int startAge) {
        this.startAge = startAge;
    }

    public int getEndAge() {
        return endAge;
    }

    public void setEndAge(int endAge) {
        this.endAge = endAge;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
