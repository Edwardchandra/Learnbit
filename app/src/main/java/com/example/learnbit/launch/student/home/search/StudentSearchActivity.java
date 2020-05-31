package com.example.learnbit.launch.student.home.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.home.search.adapter.StudentSearchHolder;
import com.example.learnbit.launch.teacher.home.adapter.CourseHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class StudentSearchActivity extends AppCompatActivity {
    private Toolbar searchToolbar;
    private RecyclerView searchRecyclerView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseRecyclerOptions<Course> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Course, StudentSearchHolder> firebaseRecyclerAdapter;

    private ArrayList<String> keyArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_search);

        searchToolbar = findViewById(R.id.studentSearch_Toolbar);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);

        setupToolbar();
        setupFirebase();
        retrieveData();
    }

    private void setupToolbar(){
        setSupportActionBar(searchToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupRecyclerView(){
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Course, StudentSearchHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull StudentSearchHolder holder, int position, @NonNull Course model) {
                holder.setCourse(keyArrayList.get(position), model);
            }

            @NonNull
            @Override
            public StudentSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_search, parent, false);

                return new StudentSearchHolder(view);
            }
        };
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        searchRecyclerView.setLayoutManager(layoutManager);
        searchRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        user = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference("Course");
    }

    private void retrieveData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Course");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    keyArrayList.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load database", Toast.LENGTH_SHORT).show();
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

                for (int i=0;i<keyArrayList.size();i++) {
                    Query query1 = databaseReference.child(keyArrayList.get(i)).orderByChild("courseName").startAt(query);
                    firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Course>().setQuery(query1, Course.class).build();

                    setupRecyclerView();

                    firebaseRecyclerAdapter.startListening();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                for (int i=0;i<keyArrayList.size();i++) {
                    Query query1 = databaseReference.child(keyArrayList.get(i)).orderByChild("courseName").startAt(newText);
                    firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Course>().setQuery(query1, Course.class).build();

                    setupRecyclerView();

                    firebaseRecyclerAdapter.startListening();
                }

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.stopListening();
        }
    }
}
