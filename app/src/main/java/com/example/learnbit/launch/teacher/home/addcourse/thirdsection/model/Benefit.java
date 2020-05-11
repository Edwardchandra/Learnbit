package com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Benefit implements Parcelable {

    private String benefit;

    public Benefit() {}

    protected Benefit(Parcel in) {
        benefit = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(benefit);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Benefit> CREATOR = new Creator<Benefit>() {
        @Override
        public Benefit createFromParcel(Parcel in) {
            return new Benefit(in);
        }

        @Override
        public Benefit[] newArray(int size) {
            return new Benefit[size];
        }
    };

    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }
}
