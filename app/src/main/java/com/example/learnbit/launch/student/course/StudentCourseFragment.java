package com.example.learnbit.launch.student.course;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.model.userdata.User;
import com.example.learnbit.launch.model.userdata.student.StudentCourse;
import com.example.learnbit.launch.student.course.adapter.MyCourseAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class StudentCourseFragment extends Fragment {

    private RecyclerView myCourseRecyclerView;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;

    private ArrayList<StudentCourse> courseArrayList = new ArrayList<>();

    private MyCourseAdapter courseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_course, container, false);

        Button myCourseSearchButton = view.findViewById(R.id.studentCourse_SearchBar);
        myCourseRecyclerView = view.findViewById(R.id.studentCourse_RecyclerView);

        setStatusBarColor();
        setupFirebase();
        retrieveData();
        setupRecyclerView();

        return view;
    }

    private void setupFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void setupRecyclerView(){
        courseAdapter = new MyCourseAdapter(courseArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        myCourseRecyclerView.setLayoutManager(layoutManager);
        myCourseRecyclerView.setAdapter(courseAdapter);
    }

    private void retrieveData(){
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(user.getUid()).child("student").child("courses");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds :  dataSnapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    StudentCourse studentCourse = ds.getValue(StudentCourse.class);

                    if (studentCourse!=null){
                        courseArrayList.add(new StudentCourse(studentCourse.getTeacherUID(), studentCourse.getCourseTime(), studentCourse.getCourseSchedule(), studentCourse.getCourseName(), studentCourse.getCourseImageURL()));
                        courseAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setStatusBarColor(){
        if (getActivity()==null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getActivity().getWindow().setStatusBarColor(Color.WHITE);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
