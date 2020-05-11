package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab.adapter.CourseReviewAdapter;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab.model.CourseReview;

import java.util.ArrayList;

public class ReviewFragment extends Fragment {

    private ProgressBar fiveStarsProgressBar, fourStarsProgressBar, threeStarsProgressBar, twoStarsProgressBar, oneStarsProgressBar;
    private RecyclerView reviewRecyclerView;
    private CourseReviewAdapter courseReviewAdapter;
    private ArrayList<CourseReview> courseReviewArrayList;

    public ReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_review, container, false);

        fiveStarsProgressBar = (ProgressBar) view.findViewById(R.id.fiveStarsProgressBar);
        fourStarsProgressBar = (ProgressBar) view.findViewById(R.id.fourStarsProgressBar);
        threeStarsProgressBar = (ProgressBar) view.findViewById(R.id.threeStarsProgressBar);
        twoStarsProgressBar = (ProgressBar) view.findViewById(R.id.twoStarsProgressBar);
        oneStarsProgressBar = (ProgressBar) view.findViewById(R.id.oneStarsProgressBar);

        reviewRecyclerView = (RecyclerView) view.findViewById(R.id.teacherCourse_ReviewRecyclerView);

        setProgressBarValues();
        addData();

        courseReviewAdapter = new CourseReviewAdapter(courseReviewArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        reviewRecyclerView.setLayoutManager(layoutManager);
        reviewRecyclerView.setAdapter(courseReviewAdapter);

        return view;
    }

    private void addData() {
        courseReviewArrayList = new ArrayList<>();
        courseReviewArrayList.add(new CourseReview(R.drawable.background_gradient, "Silvia Sanjaya", 5, "March 2, 2020", getActivity().getString(R.string.cheeseIpsum)));
        courseReviewArrayList.add(new CourseReview(R.drawable.background_gradient, "Handoko Surya", 4, "April 15, 2020", getActivity().getString(R.string.reviewIpsum)));
    }

    private void setProgressBarValues(){
        fiveStarsProgressBar.setProgress(85);
        fourStarsProgressBar.setProgress(0);
        threeStarsProgressBar.setProgress(0);
        twoStarsProgressBar.setProgress(0);
        oneStarsProgressBar.setProgress(0);
    }
}
