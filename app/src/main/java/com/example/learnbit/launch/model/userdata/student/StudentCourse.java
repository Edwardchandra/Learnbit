package com.example.learnbit.launch.model.userdata.student;

import java.util.HashMap;

public class StudentCourse {

    private String teacherUID;
    private String courseTime;
    private HashMap<String, String> courseSchedule;
    private String courseName;
    private String courseImageURL;
    private HashMap<String, String> courseDate;

    public StudentCourse(String teacherUID, String courseTime, HashMap<String, String> courseSchedule, String courseName, String courseImageURL, HashMap<String, String> courseDate) {
        this.teacherUID = teacherUID;
        this.courseTime = courseTime;
        this.courseSchedule = courseSchedule;
        this.courseName = courseName;
        this.courseImageURL = courseImageURL;
        this.courseDate = courseDate;
    }

    public StudentCourse() {
    }

    public String getTeacherUID() {
        return teacherUID;
    }

    public void setTeacherUID(String teacherUID) {
        this.teacherUID = teacherUID;
    }

    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }

    public HashMap<String, String> getCourseSchedule() {
        return courseSchedule;
    }

    public void setCourseSchedule(HashMap<String, String> courseSchedule) {
        this.courseSchedule = courseSchedule;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseImageURL() {
        return courseImageURL;
    }

    public void setCourseImageURL(String courseImageURL) {
        this.courseImageURL = courseImageURL;
    }

    public HashMap<String, String> getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(HashMap<String, String> courseDate) {
        this.courseDate = courseDate;
    }
}
