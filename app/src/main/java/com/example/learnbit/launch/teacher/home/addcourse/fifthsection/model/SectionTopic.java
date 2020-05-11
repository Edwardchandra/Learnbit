package com.example.learnbit.launch.teacher.home.addcourse.fifthsection.model;

public class SectionTopic {

    private String sectionTopicName;
    private String sectionTopicType;

    public SectionTopic(String sectionTopicName, String sectionTopicType) {
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
