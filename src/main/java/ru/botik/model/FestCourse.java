package ru.botik.model;

/**
 * Created by Daria on 19.11.2017.
 */
public class FestCourse {
    private int id;
    private int startAge;
    private int endAge;
    private String name;
    private String descr;

    public FestCourse(int id,
                      int startAge,
                      int endAge,
                      String name,
                      String descr) {
        this.id = id;
        this.startAge = startAge;
        this.endAge = endAge;
        this.name = name;
        this.descr = descr;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
