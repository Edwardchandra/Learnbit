package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Section {

    private String name;
    private HashMap<String, Content> topics;

    public Section(String name, HashMap<String, Content> topics) {
        this.name = name;
        this.topics = topics;
    }

    public Section(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Content> getTopics() {
        return topics;
    }

    public void setTopics(HashMap<String, Content> topics) {
        this.topics = topics;
    }
}
