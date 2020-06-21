package com.example.learnbit.launch.student.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.HashMap;

public class StudentHomeFragment extends Fragment implements View.OnClickListener {

    private RecyclerView categoryRecyclerView, topRatedRecyclerView;

    private ArrayList<Category> categoryArrayList;
    private ArrayList<Course> courseArrayList = new ArrayList<>();

    private CourseCardAdapter courseCardAdapter;
    private DatabaseReference databaseReference;

    private TextView nameGreeting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        topRatedRecyclerView = view.findViewById(R.id.topRatedRecyclerView);
        nameGreeting = view.findViewById(R.id.nameGreeting);
        Button searchButton = view.findViewById(R.id.student_SearchBar);

        searchButton.setOnClickListener(this);

        setStatusBarColor();
        setupFirebase();
        retrieveProfileData();
        retrieveData();
        setupRecyclerView();
        addData();

        return view;
    }

    private void setupRecyclerView(){
        categoryArrayList = new ArrayList<>();
        CategoryAdapter categoryAdapter = new CategoryAdapter(categoryArrayList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        categoryRecyclerView.setLayoutManager(layoutManager);
        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setHasFixedSize(true);

        courseCardAdapter = new CourseCardAdapter(courseArrayList);
        RecyclerView.LayoutManager topRatedLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        topRatedRecyclerView.setLayoutManager(topRatedLayoutManager);
        topRatedRecyclerView.setAdapter(courseCardAdapter);
        topRatedRecyclerView.setHasFixedSize(true);
    }

    private void setupFirebase(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Course");
    }

    private void retrieveProfileData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null){
            FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    if (name!=null){
                        if (getActivity()!=null){
                            nameGreeting.setText(getActivity().getString(R.string.name_greeting, name));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    toast(getString(R.string.retrieve_failed));
                }
            });
        }
    }

    private void retrieveData(){
        Query query = databaseReference.orderByChild("courseRating").startAt(4).endAt(5).limitToLast(5);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    Course course = ds.getValue(Course.class);
                    if (course!=null){
                        if (course.getCourseAcceptance().equalsIgnoreCase("accepted")){
                            if (course.getCourseTime()!=null){
                                if (course.getCourseTime().containsValue(false)){
                                    courseArrayList.add(new Course(key, course.getCourseName(), course.getCoursePrice(), course.getCourseImageURL(), course.getCourseStudent(), course.getCourseRating(), course.getCourseCategory()));
                                }
                            }
                        }
                    }
                    courseCardAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    //add category data
    private void addData(){
        categoryArrayList.add(new Category(R.drawable.language, "Language and Literature"));
        categoryArrayList.add(new Category(R.drawable.personal, "Personal Development"));
        categoryArrayList.add(new Category(R.drawable.computer, "Computer Technology"));
        categoryArrayList.add(new Category(R.drawable.math, "Mathematics and Logic"));
        categoryArrayList.add(new Category(R.drawable.natural, "Natural Science"));
        categoryArrayList.add(new Category(R.drawable.social, "Social \nScience"));
        categoryArrayList.add(new Category(R.drawable.art, "Art and \nCulture"));
        categoryArrayList.add(new Category(R.drawable.civil, "Civic Education"));
    }

    //element onclick action
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.student_SearchBar){
            Intent intent = new Intent(getContext(), StudentSearchActivity.class);
            startActivity(intent);
        }
    }

    //set status bar background and text color
    private void setStatusBarColor(){
        if (getActivity()==null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (getContext()!=null){
                getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.primaryColorDark));
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }

    //show toast
    private void toast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
