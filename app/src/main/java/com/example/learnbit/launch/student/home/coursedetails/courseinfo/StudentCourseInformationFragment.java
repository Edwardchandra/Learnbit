package com.example.learnbit.launch.student.home.coursedetails.courseinfo;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.model.userdata.User;
import com.example.learnbit.launch.model.userdata.teacher.Teacher;
import com.example.learnbit.launch.student.home.coursedetails.courseinfo.adapter.BenefitAdapter;
import com.example.learnbit.launch.student.home.coursedetails.courseinfo.adapter.RequirementAdapter;
import com.example.learnbit.launch.student.home.coursedetails.courseinfo.adapter.StudentSectionAdapter;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model.Section;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

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

    private Course course = new Course();

    private static final String detailPreference = "STUDENT_DETAIL_PREFERENCE";
    private String courseName, key;

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

        setupFirebase();
        getPreferenceData();
        getCurrentDateTime();
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
            courseName = preferences.getString("courseName", "");
            key = preferences.getString("key", "");
        }
    }

    private void retrieveData(){
        if (key!=null && courseName!=null){
            DatabaseReference databaseReference = firebaseDatabase.getReference("Course");
            Query query = databaseReference.child(key).orderByChild("courseName").equalTo(courseName);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference("Users").child(key).child("profileimage");

            DatabaseReference databaseReference1 = firebaseDatabase.getReference("Users").child(key);
            DatabaseReference databaseReference2 = firebaseDatabase.getReference("Users").child(key).child("teacher");
            DatabaseReference databaseReference3 = firebaseDatabase.getReference("Course").child(key);

            ValueEventListener retrieveEventListener = new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    emptyArrayList();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        course = ds.getValue(Course.class);

                        if (course != null) {
                            courseSummary.setText(course.getCourseSummary());

                            HashMap<String, String> date = course.getCourseDate();

                            try {
                                courseScheduleDate.setText(getString(R.string.divider, dateFormatter(date.get("startDate")), dateFormatter(date.get("endDate"))));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            for (HashMap.Entry<String, Boolean> entry : course.getCourseTime().entrySet()) {
                                String key = entry.getKey();

                                timeArrayList.add(key);
                            }

                            for (HashMap.Entry<String, String> entry : course.getCourseSchedule().entrySet()) {
                                String value = entry.getValue();

                                scheduleArrayList.add(value);
                            }

                            Collections.reverse(timeArrayList);
                            Collections.reverse(scheduleArrayList);

                            StringBuilder time = new StringBuilder();
                            StringBuilder schedule = new StringBuilder();

                            for (int i = 0; i < timeArrayList.size(); i++) {
                                if (i == timeArrayList.size() - 1) {
                                    time.append(timeArrayList.get(i)).append(".");
                                } else {
                                    time.append(timeArrayList.get(i)).append(", ");
                                }
                            }

                            for (int i = 0; i < scheduleArrayList.size(); i++) {
                                if (i == scheduleArrayList.size() - 1) {
                                    schedule.append(scheduleArrayList.get(i));
                                } else {
                                    schedule.append(scheduleArrayList.get(i)).append(", ");
                                }
                            }

                            courseScheduleTime.setText(getString(R.string.schedule_time, schedule.toString(), time.toString()));

                            for (HashMap.Entry<String, String> entry : course.getCourseBenefit().entrySet()) {
                                String value = entry.getValue();

                                benefitArrayList.add(value);
                            }

                            for (HashMap.Entry<String, String> entry : course.getCourseRequirement().entrySet()) {
                                String value = entry.getValue();

                                requirementArrayList.add(value);
                            }

                            Collections.reverse(benefitArrayList);
                            Collections.reverse(requirementArrayList);

                            for (HashMap.Entry<String, Section> entry : course.getCourseCurriculum().entrySet()) {
                                String key = entry.getKey();
                                Section value = entry.getValue();

                                sectionArrayList.add(new Section(key, value.getName(), value.getTopics()));
                                sectionArrayList.sort(Comparator.comparing(Section::getWeek));

                                setupRecyclerView();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };

            ValueEventListener teacherEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);

                    if (teacher != null) {
                        teacherRatings.setText(getString(R.string.teacher_rating, teacher.getRating()));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            ValueEventListener userEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        teacherName.setText(user.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            ValueEventListener courseCountEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long count = dataSnapshot.getChildrenCount();

                    teacherCourseCount.setText(getString(R.string.course_count, count));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            if (getContext()!=null){
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(getContext()).load(uri).into(teacherImageView)).addOnFailureListener(e -> Toast.makeText(getContext(), "failed to load image", Toast.LENGTH_SHORT).show());
            }

            query.addValueEventListener(retrieveEventListener);
            databaseReference1.addValueEventListener(userEventListener);
            databaseReference2.addValueEventListener(teacherEventListener);
            databaseReference3.addValueEventListener(courseCountEventListener);
        }
    }

    private void setupRecyclerView(){
        BenefitAdapter benefitAdapter = new BenefitAdapter(benefitArrayList);
        RequirementAdapter requirementAdapter = new RequirementAdapter(requirementArrayList);
        StudentSectionAdapter sectionAdapter = new StudentSectionAdapter(sectionArrayList);

        RecyclerView.LayoutManager benefitLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager requirementLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager sectionLayoutManager = new LinearLayoutManager(getContext());

        benefitRecyclerView.setLayoutManager(benefitLayoutManager);
        requirementRecyclerView.setLayoutManager(requirementLayoutManager);
        curriculumRecyclerView.setLayoutManager(sectionLayoutManager);

        benefitRecyclerView.setAdapter(benefitAdapter);
        requirementRecyclerView.setAdapter(requirementAdapter);
        curriculumRecyclerView.setAdapter(sectionAdapter);

        benefitRecyclerView.setHasFixedSize(true);
        requirementRecyclerView.setHasFixedSize(true);
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

    private void getCurrentDateTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("WIB"));

        String dateTime = simpleDateFormat.format(new java.util.Date());

        long timestampLong = System.currentTimeMillis()/1000;
        String timestamp = Long.toString(timestampLong);
    }

}
