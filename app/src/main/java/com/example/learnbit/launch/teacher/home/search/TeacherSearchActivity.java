package com.example.learnbit.launch.teacher.home.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.teacher.home.search.adapter.TeacherSearchAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherSearchActivity extends AppCompatActivity {

    //initiate element variables
    private Toolbar searchToolbar;
    private RecyclerView searchRecyclerView;

    //initiate firebase variable
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;

    //initiate adapter class variable
    private TeacherSearchAdapter teacherSearchAdapter;

    //initiate variables
    private ArrayList<Course> courseArrayList = new ArrayList<>();
    private ArrayList<String> keyArrayList = new ArrayList<>();

    //execute when activity created
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchToolbar = findViewById(R.id.teacherSearch_Toolbar);
        searchRecyclerView = findViewById(R.id.teacher_searchRecyclerView);

        setupToolbar();
        setupFirebase();
        setupRecyclerView();
        retrieveData();
    }

    //setting up custom toolbar
    private void setupToolbar() {
        setSupportActionBar(searchToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    //setting up recyclerview to display retrieved firebase database data
    private void setupRecyclerView() {
        teacherSearchAdapter = new TeacherSearchAdapter(courseArrayList, keyArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        searchRecyclerView.setLayoutManager(layoutManager);
        searchRecyclerView.setAdapter(teacherSearchAdapter);
    }

    //setting up firebase instance
    private void setupFirebase(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    //retrieve course data from firebase database
    private void retrieveData(){
        courseArrayList.clear();
        keyArrayList.clear();
        teacherSearchAdapter.notifyDataSetChanged();

        DatabaseReference databaseReference = firebaseDatabase.getReference("Course");
        Query query = databaseReference.orderByChild("teacherUid").equalTo(user.getUid());

        ValueEventListener courseEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String courseKey = ds.getKey();
                    Course course = ds.getValue(Course.class);

                    if (course!=null && courseKey!=null){
                        keyArrayList.add(courseKey);
                        courseArrayList.add(new Course(course.getCourseName(), course.getCourseAcceptance(), course.getCourseImageURL(), course.getCourseTime(), course.getCourseStudent(), course.getCourseDate()));
                    }
                }

                teacherSearchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        };

        query.addValueEventListener(courseEventListener);
    }

    //create search bar inside toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate search menu to toolbar
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.teacher_search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_menu);

        //create search view bar inside toolbar
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.teacher_home_search_hint));
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        //add edittext to search view bar
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(android.R.color.black));
        searchEditText.setHintTextColor(getResources().getColor(R.color.darkGray));

        //set search imageview icon to gone
        ImageView searchImageView = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchImageView.setVisibility(View.GONE);

        //add search clear button
        ImageView searchCloseButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchCloseButton.setVisibility(View.VISIBLE);

        //remove search underline
        View searchUnderlineView = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        searchUnderlineView.setBackgroundColor(getResources().getColor(android.R.color.white));

        //listen to search bar text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    //filter search result by text
    private void filter(String text){
        ArrayList<Course> tempArrayList = new ArrayList<>();
        for(Course course : courseArrayList){

            if(course.getCourseName().toLowerCase().contains(text)){
                tempArrayList.add(course);
            }
        }

        teacherSearchAdapter.updateList(tempArrayList);
    }

    //set back button action to return to previous activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //create a new toast
    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
