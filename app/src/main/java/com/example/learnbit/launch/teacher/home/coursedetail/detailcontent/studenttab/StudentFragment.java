package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class StudentFragment extends Fragment {

    private RecyclerView courseStudentRecyclerView;
    private TextView courseStudentNoStudentTV;

    CourseStudentAdapter courseStudentAdapter;
    ArrayList<CourseStudent> courseStudentArrayList = new ArrayList<>();

    private static final String detailPreference = "DETAIL_PREFERENCE";

    private String courseKey;

    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;

    private Course course = new Course();

    public StudentFragment() {}

    private CourseStudentAdapter.CourseStudentViewHolder courseStudentViewHolder;

    String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private static final int PERMISSIONS_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student, container, false);

        courseStudentRecyclerView = view.findViewById(R.id.teacherCourseDetailTab_StudentRecyclerView);
        courseStudentNoStudentTV = view.findViewById(R.id.teacherCourseDetail_NoStudent);

        if (getView()!=null){
            courseStudentViewHolder = new CourseStudentAdapter.CourseStudentViewHolder(getView());

            if (!checkPermission(getContext(), permissions)){
                if (getActivity()!=null){
                    ActivityCompat.requestPermissions(getActivity(), permissions, PERMISSIONS_CODE);
                }
            }else{
                Toast.makeText(getContext(), "permission granted", Toast.LENGTH_SHORT).show();
            }
        }

        setupFirebase();
        retrieveData();
        setupRecyclerView();

        return view;
    }

    private void getPreferenceData(){
        if (getActivity()!=null){
            SharedPreferences preferences = getActivity().getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
            courseKey = preferences.getString("courseKey", "");
        }
    }

    private void setupFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        user = firebaseAuth.getCurrentUser();
    }

    private void retrieveData(){
        getPreferenceData();

        DatabaseReference databaseReference = firebaseDatabase.getReference("Course");
        Query query = databaseReference.child(user.getUid()).child(courseKey);

        courseStudentArrayList.clear();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String courseKey = dataSnapshot.getKey();
                course = dataSnapshot.getValue(Course.class);

                if (course != null && courseKey != null){
                    if (course.getCourseStudent()!=null){
                        HashMap<String, String> courseStudent = course.getCourseStudent();

                        if (courseStudent.size() == 0){
                            courseStudentRecyclerView.setVisibility(View.INVISIBLE);
                            courseStudentNoStudentTV.setVisibility(View.VISIBLE);
                        }else{
                            courseStudentRecyclerView.setVisibility(View.VISIBLE);
                            courseStudentNoStudentTV.setVisibility(View.INVISIBLE);

                            for (HashMap.Entry<String, String> entry : courseStudent.entrySet()){
                                String studentUID = entry.getValue();

                                courseStudentArrayList.add(new CourseStudent(studentUID, courseKey));
                            }

                            courseStudentAdapter.notifyDataSetChanged();
                        }
                    }else{
                        courseStudentRecyclerView.setVisibility(View.INVISIBLE);
                        courseStudentNoStudentTV.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupRecyclerView(){
        courseStudentAdapter = new CourseStudentAdapter(courseStudentArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        courseStudentRecyclerView.setLayoutManager(layoutManager);
        courseStudentRecyclerView.setAdapter(courseStudentAdapter);
    }

    public static boolean checkPermission(Context context, String[] permissions){
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_CODE){
            if (grantResults.length > 0){
                courseStudentViewHolder.changeCallButton(true);
            }else{
                courseStudentViewHolder.changeCallButton(false);
                Toast.makeText(getContext(), "Please agree to the permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
