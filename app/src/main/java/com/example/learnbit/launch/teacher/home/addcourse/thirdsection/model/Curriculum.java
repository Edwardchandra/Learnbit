package com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Curriculum implements Parcelable {

    private String name;

    private String topicA;
    private String topicB;
    private String topicC;

    private String spinnerA;
    private String spinnerB;
    private String spinnerC;

    private String[] topics;
    private String[] spinners;

    public Curriculum() {}

    public Curriculum(String topicA, String topicB, String topicC, String spinnerA, String spinnerB, String spinnerC) {
        this.topicA = topicA;
        this.topicB = topicB;
        this.topicC = topicC;
        this.spinnerA = spinnerA;
        this.spinnerB = spinnerB;
        this.spinnerC = spinnerC;
    }

    private Curriculum(Parcel in) {
        name = in.readString();
        topicA = in.readString();
        topicB = in.readString();
        topicC = in.readString();
        spinnerA = in.readString();
        spinnerB = in.readString();
        spinnerC = in.readString();
        topics = in.createStringArray();
        spinners = in.createStringArray();
    }

    public static final Creator<Curriculum> CREATOR = new Creator<Curriculum>() {
        @Override
        public Curriculum createFromParcel(Parcel in) {
            return new Curriculum(in);
        }

        @Override
        public Curriculum[] newArray(int size) {
            return new Curriculum[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopicA() {
        return topicA;
    }

    public void setTopicA(String topicA) {
        this.topicA = topicA;
    }

    public String getTopicB() {
        return topicB;
    }

    public void setTopicB(String topicB) {
        this.topicB = topicB;
    }

    public String getTopicC() {
        return topicC;
    }

    public void setTopicC(String topicC) {
        this.topicC = topicC;
    }

    public String getSpinnerA() {
        return spinnerA;
    }

    public void setSpinnerA(String spinnerA) {
        this.spinnerA = spinnerA;
    }

    public String getSpinnerB() {
        return spinnerB;
    }

    public void setSpinnerB(String spinnerB) {
        this.spinnerB = spinnerB;
    }

    public String getSpinnerC() {
        return spinnerC;
    }

    public void setSpinnerC(String spinnerC) {
        this.spinnerC = spinnerC;
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public String[] getSpinners() {
        return spinners;
    }

    public void setSpinners(String[] spinners) {
        this.spinners = spinners;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(topicA);
        dest.writeString(topicB);
        dest.writeString(topicC);
        dest.writeString(spinnerA);
        dest.writeString(spinnerB);
        dest.writeString(spinnerC);
        dest.writeStringArray(topics);
        dest.writeStringArray(spinners);
    }
}
