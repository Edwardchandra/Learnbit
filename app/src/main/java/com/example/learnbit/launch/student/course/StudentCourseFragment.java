package com.example.learnbit.launch.student.course;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.course.adapter.MyCourseAdapter;
import com.example.learnbit.launch.student.course.search.MyCourseSearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class StudentCourseFragment extends Fragment implements View.OnClickListener {

    private RecyclerView myCourseRecyclerView;
    private FirebaseUser user;
    private ArrayList<Course> courseArrayList = new ArrayList<>();
    private MyCourseAdapter courseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_course, container, false);

        Button myCourseSearchButton = view.findViewById(R.id.studentCourse_SearchBar);
        myCourseRecyclerView = view.findViewById(R.id.studentCourse_RecyclerView);

        myCourseSearchButton.setOnClickListener(this);

        setStatusBarColor();
        setupFirebase();
        retrieveCourseData();
        setupRecyclerView();

        return view;
    }

    private void setupFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void setupRecyclerView(){
        courseAdapter = new MyCourseAdapter(courseArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        myCourseRecyclerView.setLayoutManager(layoutManager);
        myCourseRecyclerView.setAdapter(courseAdapter);
    }

    private void retrieveCourseData(){
        FirebaseDatabase.getInstance().getReference("Course").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    Course course = ds.getValue(Course.class);
                    if (course!=null){
                        if (course.getCourseStudent()!=null){
                            for (HashMap.Entry<String, String> entry : course.getCourseStudent().entrySet()){
                                if (entry.getValue().equals(user.getUid())){
                                    courseArrayList.add(new Course(key, course.getCourseName(), course.getCourseImageURL(), course.getCourseDate(), course.getCourseSchedule(), course.getTeacherUid()));
                                }
                            }
                        }
                        courseAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    //show toast
    private void toast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void setStatusBarColor(){
        if (getActivity()==null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getActivity().getWindow().setStatusBarColor(Color.WHITE);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.studentCourse_SearchBar){
            Intent intent = new Intent(getContext(), MyCourseSearchActivity.class);
            startActivity(intent);
        }
    }
}
