package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab.model;

public class CourseReview {

    private int reviewImage;
    private String reviewName;
    private float reviewRating;
    private String reviewDate;
    private String reviewText;

    public CourseReview(int reviewImage, String reviewName, float reviewRating, String reviewDate, String reviewText) {
        this.reviewImage = reviewImage;
        this.reviewName = reviewName;
        this.reviewRating = reviewRating;
        this.reviewDate = reviewDate;
        this.reviewText = reviewText;
    }

    public int getReviewImage() {
        return reviewImage;
    }

    public void setReviewImage(int reviewImage) {
        this.reviewImage = reviewImage;
    }

    public String getReviewName() {
        return reviewName;
    }

    public void setReviewName(String reviewName) {
        this.reviewName = reviewName;
    }

    public float getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(float reviewRating) {
        this.reviewRating = reviewRating;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}
