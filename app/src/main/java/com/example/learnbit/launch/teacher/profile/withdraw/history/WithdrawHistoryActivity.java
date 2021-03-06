package com.example.learnbit.launch.teacher.profile.withdraw.history;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.profile.withdraw.history.adapter.WithdrawHistoryAdapter;
import com.example.learnbit.launch.teacher.profile.withdraw.model.Withdraw;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WithdrawHistoryActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;

    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;

    private WithdrawHistoryAdapter withdrawHistoryAdapter;

    private ArrayList<Withdraw> withdrawArrayList = new ArrayList<>();
    private ArrayList<String> keyArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_history);

        historyRecyclerView = findViewById(R.id.withdrawHistoryRecyclerView);

        setupToolbar();
        setupFirebase();
        setupRecyclerView();
        retrieveData();
    }

    private void setupToolbar(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Withdraw History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        user = firebaseAuth.getCurrentUser();
    }

    private void retrieveData(){
        withdrawArrayList.clear();
        withdrawHistoryAdapter.notifyDataSetChanged();

        DatabaseReference databaseReference = firebaseDatabase.getReference("Withdraw");
        Query query = databaseReference.orderByChild("userUid").equalTo(user.getUid());

        ValueEventListener withdrawEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    Withdraw withdraw = ds.getValue(Withdraw.class);

                    if (key != null) {
                        keyArrayList.add(key);
                    }

                    if (withdraw != null) {
                        withdrawArrayList.add(new Withdraw(withdraw.getBankName(),
                                withdraw.getBankNumber(),
                                withdraw.getDestinationName(),
                                withdraw.getSent(),
                                withdraw.getAmount(),
                                withdraw.getUserUid(),
                                withdraw.getDateTime(),
                                withdraw.getTimestamp()));
                    }
                }

                withdrawHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(WithdrawHistoryActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
            }
        };

        query.addValueEventListener(withdrawEventListener);
    }

    private void setupRecyclerView() {
        withdrawHistoryAdapter = new WithdrawHistoryAdapter(withdrawArrayList, keyArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        historyRecyclerView.setLayoutManager(layoutManager);
        historyRecyclerView.setAdapter(withdrawHistoryAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
