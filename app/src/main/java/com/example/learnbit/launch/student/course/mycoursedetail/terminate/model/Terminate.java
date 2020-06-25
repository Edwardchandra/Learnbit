package com.example.learnbit.launch.student.course.mycoursedetail.terminate.model;

public class Terminate {

    private String reason;
    private String userUid;
    private String courseUid;
    private String dateTime;
    private long timestamp;
    private String courseTime;
    private String status;

    public Terminate() {
    }

    public Terminate(String reason, String userUid, String courseUid, String dateTime, long timestamp, String courseTime, String status) {
        this.reason = reason;
        this.userUid = userUid;
        this.courseUid = courseUid;
        this.dateTime = dateTime;
        this.timestamp = timestamp;
        this.courseTime = courseTime;
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getCourseUid() {
        return courseUid;
    }

    public void setCourseUid(String courseUid) {
        this.courseUid = courseUid;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
