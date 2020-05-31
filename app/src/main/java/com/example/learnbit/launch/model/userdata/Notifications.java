package com.example.learnbit.launch.model.userdata;

public class Notifications {

    private String title;
    private String body;
    private String dateTime;
    private String timestamp;

    public Notifications() {
    }

    public Notifications(String title, String body, String dateTime, String timestamp) {
        this.title = title;
        this.body = body;
        this.dateTime = dateTime;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
