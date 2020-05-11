package com.example.learnbit.launch.teacher.home.addcourse.secondsection.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Time implements Parcelable {

    private String time;

    public Time() {}

    protected Time(Parcel in) {
        time = in.readString();
    }

    public static final Creator<Time> CREATOR = new Creator<Time>() {
        @Override
        public Time createFromParcel(Parcel in) {
            return new Time(in);
        }

        @Override
        public Time[] newArray(int size) {
            return new Time[size];
        }
    };

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
    }
}
