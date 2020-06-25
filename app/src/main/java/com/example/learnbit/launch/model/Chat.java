package com.example.learnbit.launch.model;

public class Chat {
    private String message;
    private String userUid;
    private String dateTime;
    private long timestamp;

    public Chat(String message, String userUid, String dateTime, long timestamp) {
        this.message = message;
        this.userUid = userUid;
        this.dateTime = dateTime;
        this.timestamp = timestamp;
    }

    public Chat() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
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
}
