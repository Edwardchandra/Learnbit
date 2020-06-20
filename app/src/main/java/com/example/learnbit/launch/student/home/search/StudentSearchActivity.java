package com.example.learnbit.launch.student.home.search;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.home.search.adapter.StudentSearchAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentSearchActivity extends AppCompatActivity {

    //initiate element variables
    private Toolbar searchToolbar;
    private RecyclerView searchRecyclerView;

    //initiate firebase database variable
    private DatabaseReference databaseReference;

    //initiate variables
    private ArrayList<Course> courseArrayList = new ArrayList<>();
    private ArrayList<String> keyArrayList = new ArrayList<>();

    //initiate adapter class variable
    private StudentSearchAdapter studentSearchAdapter;

    //execute when activity created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_search);

        searchToolbar = findViewById(R.id.studentSearch_Toolbar);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);

        setupToolbar();
        setupFirebase();
        setupRecyclerView();
        retrieveKey();
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

    //setting up recyclerview to display retrieved firebae database data
    private void setupRecyclerView() {
        studentSearchAdapter = new StudentSearchAdapter(courseArrayList, keyArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        searchRecyclerView.setLayoutManager(layoutManager);
        searchRecyclerView.setAdapter(studentSearchAdapter);
    }

    //setting up firebase instance
    private void setupFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Course");
    }

    //retrieve course key from firebase database
    private void retrieveKey(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    Course course = ds.getValue(Course.class);
                    if (course!=null){
                        if (course.getCourseAcceptance().equalsIgnoreCase("accepted")){
                            keyArrayList.add(key);
                            courseArrayList.add(new Course(course.getCourseName(), course.getCoursePrice(), course.getCourseImageURL(), course.getCourseStudent(), course.getCourseRating(), course.getCourseCategory()));
                        }
                    }
                    studentSearchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.teacher_search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_menu);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.teacher_home_search_hint));
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(android.R.color.black));
        searchEditText.setHintTextColor(getResources().getColor(R.color.darkGray));

        ImageView searchImageView = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchImageView.setVisibility(View.GONE);

        ImageView searchCloseButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchCloseButton.setVisibility(View.VISIBLE);

        View searchUnderlineView = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        searchUnderlineView.setBackgroundColor(getResources().getColor(android.R.color.white));

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

    private void filter(String text){
        ArrayList<Course> tempArrayList = new ArrayList<>();
        for(Course course : courseArrayList){
            if(course.getCourseName().toLowerCase().contains(text)){
                tempArrayList.add(course);
            }
        }
        studentSearchAdapter.updateList(tempArrayList);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
