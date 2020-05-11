package com.example.learnbit.launch.teacher.home.coursedetail.adapter;

import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab.ReviewFragment;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.YourCourseFragment;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab.StudentFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class CourseDetailViewPagerAdapter extends FragmentPagerAdapter {

    public CourseDetailViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new StudentFragment();
            case 1:
                return new YourCourseFragment();
            case 2:
                return new ReviewFragment();
            default:
                return new StudentFragment();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Students";
            case 1:
                return "Courses";
            case 2:
                return "Ratings and Reviews";
            default:
                return "Students";
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
