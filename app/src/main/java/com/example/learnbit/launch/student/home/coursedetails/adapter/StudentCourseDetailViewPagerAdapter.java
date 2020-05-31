package com.example.learnbit.launch.student.home.coursedetails.adapter;

import com.example.learnbit.launch.student.home.coursedetails.courseinfo.StudentCourseInformationFragment;
import com.example.learnbit.launch.student.home.coursedetails.coursereview.StudentCourseReviewFragment;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.YourCourseFragment;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab.ReviewFragment;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab.StudentFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class StudentCourseDetailViewPagerAdapter extends FragmentPagerAdapter {

    public StudentCourseDetailViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new StudentCourseInformationFragment();
            case 1:
                return new ReviewFragment();
            default:
                return new StudentCourseInformationFragment();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Course Information";
            case 1:
                return "Ratings and Reviews";
            default:
                return "Course Information";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
