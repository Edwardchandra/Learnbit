package com.example.learnbit.launch.student.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.home.adapter.CategoryAdapter;
import com.example.learnbit.launch.student.home.adapter.CourseCardAdapter;
import com.example.learnbit.launch.student.home.model.Category;
import com.example.learnbit.launch.student.home.search.StudentSearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class StudentHomeFragment extends Fragment implements View.OnClickListener {

    private RecyclerView categoryRecyclerView, topRatedRecyclerView;
    private ArrayList<Category> categoryArrayList;

    private ArrayList<Course> courseArrayList = new ArrayList<>();
    private ArrayList<String> keyArrayList = new ArrayList<>();
    private CourseCardAdapter courseCardAdapter;

    private FirebaseDatabase firebaseDatabase;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        topRatedRecyclerView = view.findViewById(R.id.topRatedRecyclerView);
        Button searchButton = view.findViewById(R.id.student_SearchBar);

        searchButton.setOnClickListener(this);

        setupFirebase();
        retrieveData();
        setupRecyclerView();
        addData();

        setStatusBarColor();

        return view;
    }

    private void setupRecyclerView(){
        categoryArrayList = new ArrayList<>();
        CategoryAdapter categoryAdapter = new CategoryAdapter(categoryArrayList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        categoryRecyclerView.setLayoutManager(layoutManager);
        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setHasFixedSize(true);

        courseCardAdapter = new CourseCardAdapter(courseArrayList, keyArrayList);
        RecyclerView.LayoutManager topRatedLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        topRatedRecyclerView.setLayoutManager(topRatedLayoutManager);
        topRatedRecyclerView.setAdapter(courseCardAdapter);
        topRatedRecyclerView.setHasFixedSize(true);
    }

    private void setupFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void retrieveData(){
        DatabaseReference databaseReference = firebaseDatabase.getReference("Course");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key = "";

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    key = ds.getKey();

                    retrieveCourseData(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveCourseData(String key){
        DatabaseReference databaseReference = firebaseDatabase.getReference("Course").child(key);
        Query query = databaseReference.orderByChild("courseRating").startAt(4).endAt(5).limitToLast(5);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Course course = ds.getValue(Course.class);

                    if (course!=null){
                        keyArrayList.add(key);
                        courseArrayList.add(new Course(course.getCourseName(), course.getCoursePrice(), course.getCourseImageURL(), course.getCourseStudent(), course.getCourseRating()));

                        courseCardAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch database data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addData(){
        categoryArrayList.add(new Category(R.drawable.language, "Language & Literature", getResources().getStringArray(R.array.language_array)));
        categoryArrayList.add(new Category(R.drawable.personal, "Personal Development", getResources().getStringArray(R.array.personal_development_array)));
        categoryArrayList.add(new Category(R.drawable.computer, "Computer Technology", getResources().getStringArray(R.array.computer_array)));
        categoryArrayList.add(new Category(R.drawable.math, "Mathematics & Logic", getResources().getStringArray(R.array.mathematics_array)));
        categoryArrayList.add(new Category(R.drawable.natural, "Natural Science", getResources().getStringArray(R.array.natural_array)));
        categoryArrayList.add(new Category(R.drawable.social, "Social \nScience", getResources().getStringArray(R.array.social_array)));
        categoryArrayList.add(new Category(R.drawable.art, "Art & \nCulture", getResources().getStringArray(R.array.art_array)));
        categoryArrayList.add(new Category(R.drawable.civil, "Civic Education", getResources().getStringArray(R.array.civic_array)));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.student_SearchBar){
            Intent intent = new Intent(getContext(), StudentSearchActivity.class);
            startActivity(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setStatusBarColor(){
        if (getActivity()==null) return;

        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.primaryColorDark));
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }
}
