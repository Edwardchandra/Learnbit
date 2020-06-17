package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab.model;

public class CourseReview {

    private String message;
    private float rating;
    private String time;
    private String user;

    public CourseReview() {
    }

    public CourseReview(String message, float rating, String time, String user) {
        this.message = message;
        this.rating = rating;
        this.time = time;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
