package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab.adapter.CourseStudentAdapter;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab.model.CourseStudent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class StudentFragment extends Fragment {

    private RecyclerView courseStudentRecyclerView;
    private TextView courseStudentNoStudentTV;

    CourseStudentAdapter courseStudentAdapter;
    ArrayList<CourseStudent> courseStudentArrayList;

    private static final String detailPreference = "DETAIL_PREFERENCE";

    private String courseName;
    private int courseStudent;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Course course = new Course();

    public StudentFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student, container, false);

        courseStudentRecyclerView = (RecyclerView) view.findViewById(R.id.teacherCourseDetailTab_StudentRecyclerView);
        courseStudentNoStudentTV = view.findViewById(R.id.teacherCourseDetail_NoStudent);

        setupFirebase();
        retrieveData();

        courseStudentAdapter = new CourseStudentAdapter(courseStudentArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        courseStudentRecyclerView.setLayoutManager(layoutManager);
        courseStudentRecyclerView.setAdapter(courseStudentAdapter);

        return view;
    }

    private void getPreferenceData(){
        if (getActivity()!=null){
            SharedPreferences preferences = getActivity().getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
            courseName = preferences.getString("courseName", "");
        }
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        user = firebaseAuth.getCurrentUser();
    }

    private void retrieveData(){
        getPreferenceData();

        databaseReference = firebaseDatabase.getReference("Course");
        Query query = databaseReference.child(user.getUid()).orderByChild("courseName").startAt(courseName);

        Log.d("courseName", courseName);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    course = ds.getValue(Course.class);

                    if (course != null){
                        courseStudent = course.getCourseStudent();

                        Log.d("courseStudent", course.getCourseStudent() + " ");

                        if (courseStudent == 0){
                            courseStudentRecyclerView.setVisibility(View.INVISIBLE);
                            courseStudentNoStudentTV.setVisibility(View.VISIBLE);
                        }else{
                            courseStudentRecyclerView.setVisibility(View.VISIBLE);
                            courseStudentNoStudentTV.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
