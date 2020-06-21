package com.example.learnbit.launch.teacher.home.notification;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.userdata.Notification;
import com.example.learnbit.launch.teacher.home.notification.adapter.NotificationsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    //initiate element variable
    private RecyclerView notificationRecyclerView;

    private DatabaseReference databaseReference;

    //initiate variable
    private ArrayList<Notification> notificationsArrayList = new ArrayList<>();

    //initiate firebase database listener
    private ValueEventListener notificationEventListener;

    //initiate recyclerview adapter
    private NotificationsAdapter notificationsAdapter;

    //oncreate method execute when activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);

        setupToolbar();
        setupFirebase();
        setupRecyclerView();
        retrieveData();
    }

    //setting up custom toolbar
    private void setupToolbar(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Notifications");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    //settting up recyclerview adapter to populate data and layout
    private void setupRecyclerView(){
        notificationsAdapter = new NotificationsAdapter(notificationsArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        notificationRecyclerView.setLayoutManager(layoutManager);
        notificationRecyclerView.setAdapter(notificationsAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(notificationRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        notificationRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    //setup firebase instance and database references
    private void setupFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //initiate firebase variable
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user!=null){
            databaseReference = firebaseDatabase.getReference("Notification").child(user.getUid());
        }
    }

    //retrieve notification data from firebase database
    private void retrieveData(){
        Query query = databaseReference.orderByChild("timestamp");
        notificationEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationsArrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Notification notification = ds.getValue(Notification.class);
                    if (notification!=null){
                        if (notification.getRole().equalsIgnoreCase("teacher")){
                            notificationsArrayList.add(new Notification(notification.getTitle(), notification.getMessage(), notification.getRole(), notification.getTimestamp(), notification.getDateTime()));
                        }
                    }
                }

                //notify if data has changed
                notificationsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        };

        query.addValueEventListener(notificationEventListener);
    }

    //remove firebase database listener
    @Override
    protected void onStop() {
        super.onStop();

        databaseReference.orderByChild("timestamp").removeEventListener(notificationEventListener);
    }

    //execute toolbar icon action
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    //create toast
    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
