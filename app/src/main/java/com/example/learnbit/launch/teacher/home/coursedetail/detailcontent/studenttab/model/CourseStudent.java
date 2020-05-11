package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab.model;

public class CourseStudent {

    private int studentImage;
    private String studentName;
    private String studentStatus;
    private String studentTime;

    public CourseStudent(int studentImage, String studentName, String studentStatus, String studentTime) {
        this.studentImage = studentImage;
        this.studentName = studentName;
        this.studentStatus = studentStatus;
        this.studentTime = studentTime;
    }

    public int getStudentImage() {
        return studentImage;
    }

    public void setStudentImage(int studentImage) {
        this.studentImage = studentImage;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentStatus() {
        return studentStatus;
    }

    public void setStudentStatus(String studentStatus) {
        this.studentStatus = studentStatus;
    }

    public String getStudentTime() {
        return studentTime;
    }

    public void setStudentTime(String studentTime) {
        this.studentTime = studentTime;
    }
}