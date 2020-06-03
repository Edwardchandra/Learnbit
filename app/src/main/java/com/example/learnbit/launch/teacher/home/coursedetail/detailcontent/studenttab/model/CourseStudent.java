package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab.model;

public class CourseStudent {

    private String studentUID;
    private String courseKey;

    public CourseStudent(String studentUID, String courseKey) {
        this.studentUID = studentUID;
        this.courseKey = courseKey;
    }

    public CourseStudent() {
    }

    public String getCourseKey() {
        return courseKey;
    }

    public void setCourseKey(String courseKey) {
        this.courseKey = courseKey;
    }

    public String getStudentUID() {
        return studentUID;
    }

    public void setStudentUID(String studentUID) {
        this.studentUID = studentUID;
    }
}