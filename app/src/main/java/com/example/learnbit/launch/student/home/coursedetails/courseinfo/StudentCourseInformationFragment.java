package com.example.learnbit.launch.student.home.coursedetails.courseinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.home.coursedetails.courseinfo.adapter.BenefitAdapter;
import com.example.learnbit.launch.student.home.coursedetails.courseinfo.adapter.RequirementAdapter;
import com.example.learnbit.launch.student.home.coursedetails.courseinfo.adapter.StudentSectionAdapter;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model.Section;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class StudentCourseInformationFragment extends Fragment {

    private TextView teacherName, teacherCourseCount, teacherRatings, courseSummary, courseScheduleDate, courseScheduleTime;
    private ImageView teacherImageView;
    private RecyclerView benefitRecyclerView, requirementRecyclerView, curriculumRecyclerView;

    private FirebaseDatabase firebaseDatabase;

    private ArrayList<String> timeArrayList = new ArrayList<>();
    private ArrayList<String> scheduleArrayList = new ArrayList<>();
    private ArrayList<String> benefitArrayList = new ArrayList<>();
    private ArrayList<String> requirementArrayList = new ArrayList<>();
    private ArrayList<Section> sectionArrayList = new ArrayList<>();

    private static final String detailPreference = "STUDENT_DETAIL_PREFERENCE";
    private String key;

    private BenefitAdapter benefitAdapter;
    private RequirementAdapter requirementAdapter;
    private StudentSectionAdapter sectionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_student_course_information, container, false);

        courseSummary = view.findViewById(R.id.courseSummary);
        courseScheduleDate = view.findViewById(R.id.courseScheduleDate);
        courseScheduleTime = view.findViewById(R.id.courseScheduleTime);
        benefitRecyclerView = view.findViewById(R.id.benefitRecyclerView);
        requirementRecyclerView = view.findViewById(R.id.requirementRecyclerView);
        curriculumRecyclerView = view.findViewById(R.id.curriculumRecyclerView);
        teacherImageView = view.findViewById(R.id.teacherImageView);
        teacherName = view.findViewById(R.id.teacherName);
        teacherCourseCount = view.findViewById(R.id.teacherCourseCount);
        teacherRatings = view.findViewById(R.id.teacherRatings);

        teacherImageView.setClipToOutline(true);

        getPreferenceData();
        setupFirebase();
        setupRecyclerView();
        retrieveData();

        return view;
    }

    private void setupFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void emptyArrayList(){
        benefitArrayList.clear();
        requirementArrayList.clear();
        scheduleArrayList.clear();
        sectionArrayList.clear();
        timeArrayList.clear();
    }

    private void getPreferenceData(){
        if (getActivity()!=null){
            SharedPreferences preferences = getActivity().getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
            key = preferences.getString("courseKey", "");
        }
    }

    private void retrieveData(){
        if (key!=null){
            DatabaseReference databaseReference = firebaseDatabase.getReference("Course").child(key);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

            ValueEventListener retrieveEventListener = new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    emptyArrayList();
                    Course course = dataSnapshot.getValue(Course.class);
                    if (course != null) {
                        courseSummary.setText(course.getCourseSummary());

                        try {
                            courseScheduleDate.setText(getString(R.string.divider, dateFormatter(course.getCourseDate().get("startDate")), dateFormatter(course.getCourseDate().get("endDate"))));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        for (HashMap.Entry<String, Boolean> entry : course.getCourseTime().entrySet()) {
                            timeArrayList.add(entry.getKey());
                        }

                        for (HashMap.Entry<String, String> entry : course.getCourseSchedule().entrySet()) {
                            scheduleArrayList.add(entry.getValue());
                        }

                        for (HashMap.Entry<String, String> entry : course.getCourseBenefit().entrySet()) {
                            benefitArrayList.add(entry.getValue());
                        }

                        for (HashMap.Entry<String, String> entry : course.getCourseRequirement().entrySet()) {
                            requirementArrayList.add(entry.getValue());
                        }

                        Collections.reverse(timeArrayList);
                        Collections.reverse(scheduleArrayList);
                        Collections.reverse(benefitArrayList);
                        Collections.reverse(requirementArrayList);

                        StringBuilder time = new StringBuilder();
                        StringBuilder schedule = new StringBuilder();

                        for (int i=0;i<timeArrayList.size();i++) {
                            if (i == timeArrayList.size() - 1) {
                                time.append(timeArrayList.get(i)).append(".");
                            } else {
                                time.append(timeArrayList.get(i)).append(", ");
                            }
                        }

                        for (int i=0;i<scheduleArrayList.size();i++) {
                            if (i == scheduleArrayList.size() - 1) {
                                schedule.append(scheduleArrayList.get(i));
                            } else {
                                schedule.append(scheduleArrayList.get(i)).append(", ");
                            }
                        }

                        courseScheduleTime.setText(getString(R.string.schedule_time, schedule.toString(), time.toString()));

                        for (HashMap.Entry<String, Section> entry : course.getCourseCurriculum().entrySet()) {
                            sectionArrayList.add(new Section(entry.getKey(), entry.getValue().getName(), entry.getValue().getTopics()));
                            sectionArrayList.sort(Comparator.comparing(Section::getWeek));
                        }

                        benefitAdapter.notifyDataSetChanged();
                        requirementAdapter.notifyDataSetChanged();
                        sectionAdapter.notifyDataSetChanged();

                        firebaseDatabase.getReference("Users").child(course.getTeacherUid()).child("teacher").child("rating").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Float teacherRating = dataSnapshot.getValue(Float.class);
                                if (teacherRating != null) {
                                    teacherRatings.setText(getString(R.string.teacher_rating, teacherRating));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                toast(getString(R.string.retrieve_failed));
                            }
                        });

                        firebaseDatabase.getReference("Users").child(course.getTeacherUid()).child("name").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.getValue(String.class);
                                if (name!=null){
                                    teacherName.setText(name);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                toast(getString(R.string.retrieve_failed));
                            }
                        });

                        firebaseDatabase.getReference("Course").orderByChild("teacherUid").equalTo(course.getTeacherUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long courseCount = dataSnapshot.getChildrenCount();
                                teacherCourseCount.setText(getString(R.string.course_count, courseCount));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                toast(getString(R.string.retrieve_failed));
                            }
                        });

                        if (getContext()!=null){
                            firebaseStorage.getReference("Users").child(course.getTeacherUid()).child("profileimage").getDownloadUrl()
                                    .addOnSuccessListener(uri -> Glide.with(getContext()).load(uri).into(teacherImageView))
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    toast(getString(R.string.retrieve_failed));
                }
            };

            databaseReference.addValueEventListener(retrieveEventListener);
        }
    }

    private void setupRecyclerView(){
        benefitAdapter = new BenefitAdapter(benefitArrayList);
        RecyclerView.LayoutManager benefitLayoutManager = new LinearLayoutManager(getContext());
        benefitRecyclerView.setLayoutManager(benefitLayoutManager);
        benefitRecyclerView.setAdapter(benefitAdapter);
        benefitRecyclerView.setHasFixedSize(true);

        requirementAdapter = new RequirementAdapter(requirementArrayList);
        RecyclerView.LayoutManager requirementLayoutManager = new LinearLayoutManager(getContext());
        requirementRecyclerView.setLayoutManager(requirementLayoutManager);
        requirementRecyclerView.setAdapter(requirementAdapter);
        requirementRecyclerView.setHasFixedSize(true);

        sectionAdapter = new StudentSectionAdapter(sectionArrayList);
        RecyclerView.LayoutManager sectionLayoutManager = new LinearLayoutManager(getContext());
        curriculumRecyclerView.setLayoutManager(sectionLayoutManager);
        curriculumRecyclerView.setAdapter(sectionAdapter);
        curriculumRecyclerView.setHasFixedSize(true);
    }

    private String dateFormatter(String inputDate) throws ParseException {
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
        SimpleDateFormat newDateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);

        Date date = oldDateFormat.parse(inputDate);

        if (date!=null){
            return newDateFormat.format(date);
        }else {
            return inputDate;
        }
    }

    //show toast
    private void toast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
