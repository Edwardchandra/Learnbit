package com.example.learnbit.launch.model.userdata;

public class Notification {
    private String title;
    private String message;
    private String role;
    private String timestamp;
    private String dateTime;

    public Notification(String title, String message, String role, String timestamp, String dateTime) {
        this.title = title;
        this.message = message;
        this.role = role;
        this.timestamp = timestamp;
        this.dateTime = dateTime;
    }

    public Notification() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
