package com.example.learnbit.launch.student.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.example.learnbit.launch.student.home.adapter.CategoryAdapter;
import com.example.learnbit.launch.student.home.adapter.CourseCardHolder;
import com.example.learnbit.launch.student.home.model.Category;
import com.example.learnbit.launch.student.home.search.StudentSearchActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class StudentHomeFragment extends Fragment implements View.OnClickListener {

    private RecyclerView categoryRecyclerView, topRatedRecyclerView;
    private Button searchButton;
    private ArrayList<Category> categoryArrayList;
    private FirebaseRecyclerOptions<Course> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Course, CourseCardHolder> firebaseRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        topRatedRecyclerView = view.findViewById(R.id.topRatedRecyclerView);
        searchButton = view.findViewById(R.id.student_SearchBar);

        searchButton.setOnClickListener(this);

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

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Course, CourseCardHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CourseCardHolder holder, int position, @NonNull Course model) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Course");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            holder.setCourse(ds.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to load database", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public CourseCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.item_course_card, parent, false);

                return new CourseCardHolder(view);
            }
        };

        RecyclerView.LayoutManager topRatedLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        topRatedRecyclerView.setLayoutManager(topRatedLayoutManager);
        topRatedRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void retrieveData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query query = firebaseDatabase.getReference("Course");

        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Course>().setQuery(query, Course.class).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.stopListening();
        }
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
}
