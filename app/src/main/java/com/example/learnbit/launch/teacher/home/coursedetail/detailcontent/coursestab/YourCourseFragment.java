package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.adapter.SectionAdapter;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model.Content;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model.Section;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class YourCourseFragment extends Fragment {

    private RecyclerView sectionRecyclerView;
    private TextView courseSummary;

    private static final String detailPreference = "DETAIL_PREFERENCE";
    private String courseName;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ArrayList<Section> sectionArrayList = new ArrayList<>();

    public YourCourseFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_your_course, container, false);

        courseSummary = view.findViewById(R.id.teacherCourse_Summary);
        sectionRecyclerView = view.findViewById(R.id.teacherCourse_SectionRecyclerView);

        setupFirebase();
        retrieveData();

        return view;
    }

    private void setupRecyclerView(){
        SectionAdapter sectionAdapter = new SectionAdapter(sectionArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        sectionRecyclerView.setLayoutManager(layoutManager);
        sectionRecyclerView.setAdapter(sectionAdapter);
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

//        firebaseRecyclerOptions  = new FirebaseRecyclerOptions.Builder<Course>().setQuery(query, Course.class).build();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Course course = ds.getValue(Course.class);

                    if (course!=null){
                        Log.d("course", course.getCourseCurriculum() + " ");

                        for (HashMap.Entry<String, Section> entry : course.getCourseCurriculum().entrySet()) {
                            String key = entry.getKey();
                            Section value = entry.getValue();

                            Log.d("topic2", value.getName() + " ");
                            Log.d("topic2", value.getTopics() + " ");

                            sectionArrayList.add(new Section(value.getName(), value.getTopics()));
                            Collections.reverse(sectionArrayList);

                            Log.d("section", sectionArrayList + " ");

                            for (HashMap.Entry<String, Content> entry1 : value.getTopics().entrySet()){
                                Content value1 = entry1.getValue();

                                Log.d("topic2", value1.getSectionTopicName() + " ");
                            }
                        }

                        setupRecyclerView();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
