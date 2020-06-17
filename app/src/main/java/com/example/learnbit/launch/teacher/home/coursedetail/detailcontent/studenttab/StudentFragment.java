package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class StudentFragment extends Fragment {

    //initiate elements variables
    private RecyclerView courseStudentRecyclerView;
    private TextView courseStudentNoStudentTV;

    //initiate recyclerview adapter class
    private CourseStudentAdapter courseStudentAdapter;

    //initiate variable
    ArrayList<CourseStudent> courseStudentArrayList = new ArrayList<>();
    private String courseKey;

    //initiate preference key to retrieve Shared Preference data
    private static final String detailPreference = "DETAIL_PREFERENCE";

    //initiate firebase variables
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;

    //empty constructor
    public StudentFragment() {}

    //oncreateview execute when fragment is created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student, container, false);

        courseStudentRecyclerView = view.findViewById(R.id.teacherCourseDetailTab_StudentRecyclerView);
        courseStudentNoStudentTV = view.findViewById(R.id.teacherCourseDetail_NoStudent);

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
        firebaseDatabase = FirebaseDatabase.getInstance();

        user = firebaseAuth.getCurrentUser();
    }

    //retrieve student data from firebase database and store to arraylist
    private void retrieveData(){

        //clear arraylist before attempt to retrieve data
        courseStudentArrayList.clear();
        courseStudentAdapter.notifyDataSetChanged();

        //get reference path to stored data
        DatabaseReference databaseReference = firebaseDatabase.getReference("Course").child(user.getUid()).child(courseKey);

        //add listener to listen to data
        //value event listener always listen to data, if there's data change in firebase database, data displayed automatically change
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve course data
                String courseKey = dataSnapshot.getKey();
                Course course = dataSnapshot.getValue(Course.class);

                //check if course and coursekey is not null
                if (course != null && courseKey != null){

                    //get current time
                    Date currentTime = Calendar.getInstance().getTime();

                    //initiate new date
                    Date startDate = new Date();
                    Date endDate = new Date();

                    //date formatter
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);

                    for (HashMap.Entry<String, String> entry : course.getCourseDate().entrySet()){
                        if (entry.getKey().equals("startDate")){
                            try {
                                startDate = simpleDateFormat1.parse(entry.getValue());
                            }catch (ParseException e){
                                e.printStackTrace();
                            }
                        }else if (entry.getKey().equals("endDate")){
                            try {
                                endDate = simpleDateFormat1.parse(entry.getValue());
                            }catch (ParseException e){
                                e.printStackTrace();
                            }
                        }
                    }

                    if (currentTime.before(endDate)){
                        //check if course student is not null
                        if (course.getCourseStudent()!=null && course.getCourseStudent().size()!=0){
                            courseStudentRecyclerView.setVisibility(View.VISIBLE);
                            courseStudentNoStudentTV.setVisibility(View.INVISIBLE);

                            //populate arraylist
                            for (HashMap.Entry<String, String> entry : course.getCourseStudent().entrySet()){
                                String studentUID = entry.getValue();

                                courseStudentArrayList.add(new CourseStudent(studentUID, courseKey));
                            }

                            //notify if data changed
                            courseStudentAdapter.notifyDataSetChanged();
                        }else{
                            courseStudentRecyclerView.setVisibility(View.INVISIBLE);
                            courseStudentNoStudentTV.setVisibility(View.VISIBLE);
                        }
                    }else if (!currentTime.before(endDate)){
                        courseStudentRecyclerView.setVisibility(View.VISIBLE);
                        courseStudentNoStudentTV.setVisibility(View.INVISIBLE);
                        courseStudentNoStudentTV.setText(getString(R.string.course_end_period));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    //setup recyclerview
    private void setupRecyclerView(){
        courseStudentAdapter = new CourseStudentAdapter(courseStudentArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        courseStudentRecyclerView.setLayoutManager(layoutManager);
        courseStudentRecyclerView.setAdapter(courseStudentAdapter);
    }

    //show toast
    private void toast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
