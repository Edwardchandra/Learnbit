package com.example.learnbit.launch.teacher.home.notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.userdata.Notifications;
import com.example.learnbit.launch.teacher.home.notification.adapter.NotificationsAdapter;
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

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notificationRecyclerView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;

    private ArrayList<Notifications> notificationsArrayList = new ArrayList<>();
    private NotificationsAdapter notificationsAdapter;

    private ValueEventListener notificationEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);

        setupToolbar();
        setupFirebase();
        retrieveData();
    }

    private void setupToolbar(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Notifications");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupRecyclerView(){
        notificationsAdapter = new NotificationsAdapter(notificationsArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        notificationRecyclerView.setLayoutManager(layoutManager);
        notificationRecyclerView.setAdapter(notificationsAdapter);
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        user = firebaseAuth.getCurrentUser();
    }

    private void retrieveData(){
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(user.getUid()).child("teacher").child("notifications");
        Query query = databaseReference.orderByChild("timestamp").limitToLast(10);

        notificationEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationsArrayList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Notifications notifications = ds.getValue(Notifications.class);

                    if (notifications!=null){
                        notificationsArrayList.add(new Notifications(notifications.getTitle(), notifications.getBody(), notifications.getDateTime(), notifications.getTimestamp()));
                    }
                }

                setupRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        query.addValueEventListener(notificationEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        firebaseDatabase.getReference("Users").child(user.getUid()).child("teacher").child("notifications").orderByChild("timestamp").limitToLast(10).removeEventListener(notificationEventListener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
