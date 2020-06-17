package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab.adapter.CourseReviewAdapter;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab.model.CourseReview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReviewFragment extends Fragment {

    //initiate elements' variable
    private ProgressBar fiveStarsProgressBar, fourStarsProgressBar, threeStarsProgressBar, twoStarsProgressBar, oneStarsProgressBar;
    private TextView ratingCount, ratingText, fiveStarTV, fourStarTV, threeStarTV, twoStarTV, oneStarTV, noReview;
    private RatingBar ratingBar;
    private RecyclerView reviewRecyclerView;
    private ConstraintLayout reviewLayout;

    //initiate recyclerview adapter
    private CourseReviewAdapter courseReviewAdapter;

    //initiate variables
    private ArrayList<CourseReview> courseReviewArrayList = new ArrayList<>();
    private String courseKey;

    //initiate firebase variables
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;

    //initiate preference key to retrieve Shared Preference data
    private static final String detailPreference = "DETAIL_PREFERENCE";

    //constructor
    public ReviewFragment() {}

    //oncreateview execute when fragment created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_review, container, false);

        fiveStarsProgressBar = view.findViewById(R.id.fiveStarsProgressBar);
        fourStarsProgressBar = view.findViewById(R.id.fourStarsProgressBar);
        threeStarsProgressBar = view.findViewById(R.id.threeStarsProgressBar);
        twoStarsProgressBar = view.findViewById(R.id.twoStarsProgressBar);
        oneStarsProgressBar = view.findViewById(R.id.oneStarsProgressBar);
        fiveStarTV = view.findViewById(R.id.fiveStarsTV);
        fourStarTV = view.findViewById(R.id.fourStarsTV);
        threeStarTV = view.findViewById(R.id.threeStarsTV);
        twoStarTV = view.findViewById(R.id.twoStarsTV);
        oneStarTV = view.findViewById(R.id.oneStarsTV);
        reviewRecyclerView = view.findViewById(R.id.teacherCourse_ReviewRecyclerView);
        ratingCount = view.findViewById(R.id.ratingCount);
        ratingBar = view.findViewById(R.id.ratingBar);
        ratingText = view.findViewById(R.id.ratingText);
        reviewLayout = view.findViewById(R.id.reviewView);
        noReview = view.findViewById(R.id.teacherCourseDetail_NoReview);

        getPreferenceData();
        setupFirebase();
        setupRecyclerView();
        retrieveData();

        return view;
    }

    //retrieve stored shared preference data
    private void getPreferenceData(){
        if (getActivity()!=null){
            SharedPreferences preferences = getActivity().getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
            courseKey = preferences.getString("courseKey", "");
        }
    }

    //setup firebase instance
    private void setupFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    //retrieve data from firebase database
    //[FOR COMPLETE UNDERSTANDING PLEASE SEE THE FIREBASE DATABASE STRUCTURE]
    private void retrieveData(){
        courseReviewArrayList.clear();
        courseReviewAdapter.notifyDataSetChanged();
        ratingBar.setRating(0);

        DatabaseReference databaseReference = firebaseDatabase.getReference("Rating").child(courseKey);
        DatabaseReference databaseReference1 = firebaseDatabase.getReference("Course").child(user.getUid()).child(courseKey).child("courseRating");

        //retrieve rating from course branch
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Float courseRating = dataSnapshot.getValue(Float.class);

                if (courseRating!=null){
                    ratingText.setText(getString(R.string.rating_text, courseRating));
                    ratingBar.setRating(courseRating);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });

        //retrieve review from rating branch
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int fiveStarCount = 0, fourStarCount = 0, threeStarCount = 0, twoStarCount = 0, oneStarCount = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    CourseReview courseReview = ds.getValue(CourseReview.class);

                    if (courseReview!=null) {
                        courseReviewArrayList.add(new CourseReview(courseReview.getMessage(), courseReview.getRating(), courseReview.getTime(), courseReview.getUser()));

                        if (courseReview.getRating() == 5){
                            fiveStarCount = fiveStarCount + 1;
                        }else if (courseReview.getRating() == 4){
                            fourStarCount = fourStarCount + 1;
                        }else if (courseReview.getRating() == 3){
                            threeStarCount = threeStarCount + 1;
                        }else if (courseReview.getRating() == 2){
                            twoStarCount = twoStarCount + 1;
                        }else if (courseReview.getRating() == 1){
                            oneStarCount = oneStarCount + 1;
                        }
                    }
                }

                ratingCount.setText(getString(R.string.rating_count, courseReviewArrayList.size()));
                fiveStarsProgressBar.setProgress(fiveStarCount);
                fourStarsProgressBar.setProgress(fourStarCount);
                threeStarsProgressBar.setProgress(threeStarCount);
                twoStarsProgressBar.setProgress(twoStarCount);
                oneStarsProgressBar.setProgress(oneStarCount);
                fiveStarTV.setText(getString(R.string.rating_progress_bar, fiveStarCount));
                fourStarTV.setText(getString(R.string.rating_progress_bar, fourStarCount));
                threeStarTV.setText(getString(R.string.rating_progress_bar, threeStarCount));
                twoStarTV.setText(getString(R.string.rating_progress_bar, twoStarCount));
                oneStarTV.setText(getString(R.string.rating_progress_bar, oneStarCount));
                courseReviewAdapter.notifyDataSetChanged();

                if (courseReviewArrayList.size()!=0){
                    noReview.setVisibility(View.INVISIBLE);
                    reviewLayout.setVisibility(View.VISIBLE);
                }else{
                    noReview.setVisibility(View.VISIBLE);
                    reviewLayout.setVisibility(View.INVISIBLE);
                }

                fiveStarsProgressBar.setMax(courseReviewArrayList.size());
                fourStarsProgressBar.setMax(courseReviewArrayList.size());
                threeStarsProgressBar.setMax(courseReviewArrayList.size());
                twoStarsProgressBar.setMax(courseReviewArrayList.size());
                oneStarsProgressBar.setMax(courseReviewArrayList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    //setup recyclerview layout type and adapter
    private void setupRecyclerView(){
        courseReviewAdapter = new CourseReviewAdapter(courseReviewArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        reviewRecyclerView.setLayoutManager(layoutManager);
        reviewRecyclerView.setAdapter(courseReviewAdapter);
    }

    //show toast
    private void toast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
