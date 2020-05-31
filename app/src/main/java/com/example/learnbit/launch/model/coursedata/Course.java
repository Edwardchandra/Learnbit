package com.example.learnbit.launch.model.coursedata;

import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model.Section;

import java.util.HashMap;

public class Course {

    private String courseName;
    private String courseSummary;
    private long coursePrice;
    private String courseAcceptance;
    private String courseCategory;
    private String courseSubcategory;
    private String courseImageURL;
    private HashMap<String, String> courseDate;
    private HashMap<String, Boolean> courseTime;
    private HashMap<String, Section> courseCurriculum;
    private HashMap<String, String> courseBenefit;
    private HashMap<String, String> courseRequirement;
    private HashMap<String, String> courseSchedule;
    private HashMap<String, String> courseStudent;
    private String createTime;
    private String timestamp;
    private float courseRating;

    public Course(String courseName, String courseSummary, long coursePrice, String courseAcceptance, String courseCategory, String courseSubcategory, String courseImageURL, String createTime, String timestamp, float courseRating) {
        this.courseName = courseName;
        this.courseSummary = courseSummary;
        this.coursePrice = coursePrice;
        this.courseAcceptance = courseAcceptance;
        this.courseCategory = courseCategory;
        this.courseSubcategory = courseSubcategory;
        this.courseImageURL = courseImageURL;
        this.createTime = createTime;
        this.timestamp = timestamp;
        this.courseRating = courseRating;
    }

    public Course(String courseName, long coursePrice, String courseImageURL, float courseRating) {
        this.courseName = courseName;
        this.coursePrice = coursePrice;
        this.courseImageURL = courseImageURL;
        this.courseRating = courseRating;
    }

    public Course() {}

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseSummary() {
        return courseSummary;
    }

    public void setCourseSummary(String courseSummary) {
        this.courseSummary = courseSummary;
    }

    public long getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(long coursePrice) {
        this.coursePrice = coursePrice;
    }

    public String getCourseAcceptance() {
        return courseAcceptance;
    }

    public void setCourseAcceptance(String courseAcceptance) {
        this.courseAcceptance = courseAcceptance;
    }

    public String getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }

    public String getCourseSubcategory() {
        return courseSubcategory;
    }

    public void setCourseSubcategory(String courseSubcategory) {
        this.courseSubcategory = courseSubcategory;
    }

    public String getCourseImageURL() {
        return courseImageURL;
    }

    public void setCourseImageURL(String courseImageURL) {
        this.courseImageURL = courseImageURL;
    }

    public HashMap<String, Boolean> getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(HashMap<String, Boolean> courseTime) {
        this.courseTime = courseTime;
    }

    public HashMap<String, Section> getCourseCurriculum() {
        return courseCurriculum;
    }

    public void setCourseCurriculum(HashMap<String, Section> courseCurriculum) {
        this.courseCurriculum = courseCurriculum;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public HashMap<String, String> getCourseSchedule() {
        return courseSchedule;
    }

    public void setCourseSchedule(HashMap<String, String> courseSchedule) {
        this.courseSchedule = courseSchedule;
    }

    public float getCourseRating() {
        return courseRating;
    }

    public void setCourseRating(float courseRating) {
        this.courseRating = courseRating;
    }

    public HashMap<String, String> getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(HashMap<String, String> courseDate) {
        this.courseDate = courseDate;
    }

    public HashMap<String, String> getCourseBenefit() {
        return courseBenefit;
    }

    public void setCourseBenefit(HashMap<String, String> courseBenefit) {
        this.courseBenefit = courseBenefit;
    }

    public HashMap<String, String> getCourseRequirement() {
        return courseRequirement;
    }

    public void setCourseRequirement(HashMap<String, String> courseRequirement) {
        this.courseRequirement = courseRequirement;
    }

    public HashMap<String, String> getCourseStudent() {
        return courseStudent;
    }

    public void setCourseStudent(HashMap<String, String> courseStudent) {
        this.courseStudent = courseStudent;
    }
}
