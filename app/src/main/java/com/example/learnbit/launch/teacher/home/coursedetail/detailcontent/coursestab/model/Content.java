package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model;

public class Content {

    private String sectionTopicName;
    private String sectionTopicType;

    public Content() {
    }

    public Content(String sectionTopicName, String sectionTopicType) {
        this.sectionTopicName = sectionTopicName;
        this.sectionTopicType = sectionTopicType;
    }

    public String getSectionTopicName() {
        return sectionTopicName;
    }

    public void setSectionTopicName(String sectionTopicName) {
        this.sectionTopicName = sectionTopicName;
    }

    public String getSectionTopicType() {
        return sectionTopicType;
    }

    public void setSectionTopicType(String sectionTopicType) {
        this.sectionTopicType = sectionTopicType;
    }
}
