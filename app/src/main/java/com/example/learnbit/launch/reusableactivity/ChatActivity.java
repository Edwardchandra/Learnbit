package com.example.learnbit.launch.reusableactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.Chat;
import com.example.learnbit.launch.reusableactivity.adapter.ChatAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView chatRecyclerView;
    private EditText chatMessage;
    private String courseKey;
    private static final String detailPreference = "DETAIL_PREFERENCE";
    private ChatAdapter chatAdapter;
    private ArrayList<Chat> chatArrayList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String dateTime;
    private long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chatRV);
        chatMessage = findViewById(R.id.chatET);
        Button sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(this);

        setupToolbar();
        getPreferenceData();
        getCurrentDateTime();
        setupFirebase();
        setupRecyclerView();
        retrieveChat();
    }

    private void setupRecyclerView(){
        chatAdapter = new ChatAdapter(chatArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupFirebase(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(courseKey).child("message");
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void retrieveChat(){
        Query query = databaseReference;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatArrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if (chat!=null){
                        chatArrayList.add(new Chat(chat.getMessage(), chat.getUserUid(), chat.getDateTime(), chat.getTimestamp()));
                    }
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(){
        databaseReference.push().setValue(new Chat(chatMessage.getText().toString(), user.getUid(), dateTime, timestamp)).addOnSuccessListener(aVoid -> {
            chatMessage.setText("");
        });
    }

    //retrieve stored shared preference data
    private void getPreferenceData(){
        SharedPreferences preferences = getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
        courseKey = preferences.getString("courseKey", "");
    }

    private void getCurrentDateTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.ENGLISH);
        dateTime = simpleDateFormat.format(new java.util.Date());
        timestamp = System.currentTimeMillis()/1000;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.sendButton){
            if (!chatMessage.getText().toString().isEmpty()){
                sendMessage();
            }
        }
    }

    private void setupToolbar(){
        if (getSupportActionBar() != null){
            setTitle("Discussion Room");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}