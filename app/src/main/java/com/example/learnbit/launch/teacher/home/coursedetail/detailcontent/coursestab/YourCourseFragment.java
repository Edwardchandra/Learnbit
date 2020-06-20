package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.adapter.SectionAdapter;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model.Section;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class YourCourseFragment extends Fragment {

    //initiate elements' variable
    private RecyclerView sectionRecyclerView;
    private TextView courseSummary;

    //initiate preference key to retrieve shared preference data
    private static final String detailPreference = "DETAIL_PREFERENCE";

    private FirebaseDatabase firebaseDatabase;

    //initiate recyclerview section adapter
    private SectionAdapter sectionAdapter;

    //initiate variables
    private ArrayList<Section> sectionArrayList = new ArrayList<>();
    private String courseKey;

    //constructor
    public YourCourseFragment() {}

    //execute when fragment is created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_your_course, container, false);

        courseSummary = view.findViewById(R.id.teacherCourse_Summary);
        sectionRecyclerView = view.findViewById(R.id.teacherCourse_SectionRecyclerView);

        getPreferenceData();
        setupFirebase();
        setupRecyclerView();
        retrieveData();

        return view;
    }

    //setup recyclerview layout tipe and adapter
    private void setupRecyclerView(){
        sectionAdapter = new SectionAdapter(sectionArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        sectionRecyclerView.setLayoutManager(layoutManager);
        sectionRecyclerView.setAdapter(sectionAdapter);
    }

    //retrieve stored data from shared preference
    private void getPreferenceData(){
        String preferenceKey = "courseKey";

        if (getActivity()!=null){
            SharedPreferences preferences = getActivity().getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
            courseKey = preferences.getString(preferenceKey, "");
        }
    }

    //setup firebase instance
    private void setupFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    //retrieve course data from firebase database
    private void retrieveData(){
        sectionArrayList.clear();
        sectionAdapter.notifyDataSetChanged();

        DatabaseReference databaseReference = firebaseDatabase.getReference("Course").child(courseKey);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Course course = dataSnapshot.getValue(Course.class);

                if (course!=null){
                    courseSummary.setText(course.getCourseSummary());

                    for (HashMap.Entry<String, Section> entry : course.getCourseCurriculum().entrySet()) {
                        sectionArrayList.add(new Section(entry.getKey(), entry.getValue().getName(), entry.getValue().getTopics()));
                        sectionArrayList.sort(Comparator.comparing(Section::getWeek));
                    }
                }

                sectionAdapter.notifyDataSetChanged();
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

}
