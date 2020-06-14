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

import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.teacher.home.adapter.CourseHolder;
import com.example.learnbit.launch.teacher.home.addcourse.firstsection.AddFirstSectionActivity;
import com.example.learnbit.launch.teacher.home.notification.NotificationActivity;
import com.example.learnbit.launch.teacher.home.search.TeacherSearchActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class TeacherHomeFragment extends Fragment implements View.OnClickListener {

    private RecyclerView teacherHomeRecyclerView;

    //initiate firebase variable
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;

    //options and adapter variable is used to retrieve firebase database data and populate the data to recyclerview
    private FirebaseRecyclerOptions<Course> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Course, CourseHolder> firebaseRecyclerAdapter;

    // Required empty public constructor
    public TeacherHomeFragment() { }

    //onCreateView execute when fragment is created
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflate layout file to fragment
        final View view = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        //initiate element variables from layout view
        teacherHomeRecyclerView = view.findViewById(R.id.teacherHome_RecyclerView);
        Button teacherHomeSearchBar = view.findViewById(R.id.teacherHome_SearchBar);
        FloatingActionButton teacherHomeAddCourseButton = view.findViewById(R.id.teacherHome_AddCourseButton);
        Button notificationButton = view.findViewById(R.id.teacherHome_NotificationButton);

        //bring notification button to top of all view
        notificationButton.bringToFront();

        //set elements onClick listener to override method
        teacherHomeSearchBar.setOnClickListener(this);
        teacherHomeAddCourseButton.setOnClickListener(this);
        notificationButton.setOnClickListener(this);

        setupFirebase();
        retrieveDataFromFirebase();
        setupRecyclerView();

        return view;
    }

    //setup recyclerview to show items
    private void setupRecyclerView(){
        //populate recyclerview elements with retrieved data
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Course, CourseHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CourseHolder holder, int position, @NonNull Course model) {

                //get key of the current branch
                String key = firebaseRecyclerAdapter.getRef(position).getKey();

                //get current branch children and populate to recycler view item
                holder.setCourse(model, key);
            }

            @NonNull
            @Override
            public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                //inflate layout file to recyclerview adapter
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_course_list, parent, false);

                return new CourseHolder(view);
            }
        };

        //setting up layout type
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        //apply layout tipe to recyclerview
        teacherHomeRecyclerView.setLayoutManager(layoutManager);

        //apply adapter to recyclerview
        teacherHomeRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    //setup firebase instance
    private void setupFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        user = firebaseAuth.getCurrentUser();
    }

    //setup firebase database branch to retrieve value
    private void retrieveDataFromFirebase(){
        DatabaseReference databaseReference = firebaseDatabase.getReference("Course");
        Query query = databaseReference.child(user.getUid());

        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Course>().setQuery(query, Course.class).build();
    }

    //action of elements defined here
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
                Intent searchIntent = new Intent(getContext(), TeacherSearchActivity.class);
                startActivity(searchIntent);
                break;
        }
    }

    //when fragment is started
    @Override
    public void onStart() {
        super.onStart();

        //start listening to firebase database
        firebaseRecyclerAdapter.startListening();
    }

    //when fragment is stopped
    @Override
    public void onStop() {
        super.onStop();

        //check if firebase adapter is not null
        if (firebaseRecyclerAdapter!= null) {

            //stop listening to firebase database
            firebaseRecyclerAdapter.stopListening();
        }
    }
}
