package com.example.learnbit.launch.teacher.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.teacher.home.adapter.CourseHolder;
import com.example.learnbit.launch.teacher.home.addcourse.firstsection.AddFirstSectionActivity;
import com.example.learnbit.launch.teacher.home.notification.NotificationActivity;
import com.example.learnbit.launch.teacher.home.search.SearchActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class TeacherHomeFragment extends Fragment implements View.OnClickListener {

    private Button teacherHomeSearchBar;
    private RecyclerView teacherHomeRecyclerView;
    private Button teacherHomeNotificationButton;
    private FloatingActionButton teacherHomeAddCourseButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private FirebaseRecyclerOptions<Course> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Course, CourseHolder> firebaseRecyclerAdapter;

    // Required empty public constructor
    public TeacherHomeFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        teacherHomeRecyclerView = view.findViewById(R.id.teacherHome_RecyclerView);
        teacherHomeSearchBar = view.findViewById(R.id.teacherHome_SearchBar);
        teacherHomeAddCourseButton = view.findViewById(R.id.teacherHome_AddCourseButton);
        Button notificationButton = view.findViewById(R.id.teacherHome_NotificationButton);
        notificationButton.bringToFront();

        teacherHomeSearchBar.setOnClickListener(this);
        teacherHomeAddCourseButton.setOnClickListener(this);
        notificationButton.setOnClickListener(this);

        setupFirebase();
        retrieveDataFromFirebase();
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView(){
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Course, CourseHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CourseHolder holder, int position, @NonNull Course model) {
                holder.setCourse(model);
            }

            @NonNull
            @Override
            public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_course_list, parent, false);

                return new CourseHolder(view);
            }
        };
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        teacherHomeRecyclerView.setLayoutManager(layoutManager);
        teacherHomeRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        user = firebaseAuth.getCurrentUser();
    }

    private void retrieveDataFromFirebase(){
        databaseReference = firebaseDatabase.getReference("Course");
        Query query = databaseReference.child(user.getUid());

        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Course>().setQuery(query, Course.class).build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.teacherHome_NotificationButton:
                Intent notificationIntent = new Intent(getContext(), NotificationActivity.class);
                startActivity(notificationIntent);
                break;
            case R.id.teacherHome_AddCourseButton:
                Intent intent = new Intent(getContext(), AddFirstSectionActivity.class);
                startActivity(intent);
                break;
            case R.id.teacherHome_SearchBar:
                Intent searchIntent = new Intent(getContext(), SearchActivity.class);
                startActivity(searchIntent);
//                if (getActivity() != null){
//                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                }
                break;
            default:
                Toast.makeText(getContext(), "nothing happened", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (firebaseRecyclerAdapter!= null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }
}
