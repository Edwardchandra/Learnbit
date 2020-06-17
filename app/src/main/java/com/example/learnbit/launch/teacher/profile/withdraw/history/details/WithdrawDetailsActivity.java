package com.example.learnbit.launch.teacher.profile.withdraw.history.details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.profile.withdraw.model.Withdraw;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WithdrawDetailsActivity extends AppCompatActivity {

    private TextView status, name, amount, bankName, bankNumber, bankHolder, dateTime;
    private FirebaseDatabase firebaseDatabase;

    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_details);

        status = findViewById(R.id.status);
        name = findViewById(R.id.withdrawName);
        amount = findViewById(R.id.withdrawAmount);
        bankName = findViewById(R.id.bankName);
        bankNumber = findViewById(R.id.bankNumber);
        bankHolder = findViewById(R.id.bankHolder);
        dateTime = findViewById(R.id.dateTime);

        key = getIntent().getStringExtra("key");

        setupToolbar();
        retrieveData();
    }

    private void retrieveData(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (key!=null){
            final DatabaseReference databaseReference = firebaseDatabase.getReference("Withdraw").child(key);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Withdraw withdraw = dataSnapshot.getValue(Withdraw.class);

                    if (withdraw!=null){
                        switch (withdraw.getSent()) {
                            case "pending":
                                status.setText(getString(R.string.pending_status));
                                break;
                            case "processing":
                                status.setText(getString(R.string.processing_status));
                                break;
                            case "success":
                                status.setText(getString(R.string.success_status));
                                break;
                            case "failed":
                                status.setText(getString(R.string.failed_status));
                                break;
                        }

                        firebaseDatabase.getReference("Users").child(withdraw.getUserUid()).child("name").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String nameValue = dataSnapshot.getValue(String.class);

                                if (nameValue!=null){
                                    name.setText(nameValue);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(WithdrawDetailsActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
                            }
                        });

                        amount.setText(String.valueOf(withdraw.getAmount()));
                        bankName.setText(withdraw.getBankName());
                        bankNumber.setText(String.valueOf(withdraw.getBankNumber()));
                        bankHolder.setText(withdraw.getDestinationName());
                        dateTime.setText(withdraw.getDateTime());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(WithdrawDetailsActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupToolbar(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Withdraw Detail");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
