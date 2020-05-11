package com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Requirement implements Parcelable {

    private String requirement;

    public Requirement() {}

    protected Requirement(Parcel in) {
        requirement = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(requirement);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Requirement> CREATOR = new Creator<Requirement>() {
        @Override
        public Requirement createFromParcel(Parcel in) {
            return new Requirement(in);
        }

        @Override
        public Requirement[] newArray(int size) {
            return new Requirement[size];
        }
    };

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }
}
