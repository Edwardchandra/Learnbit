package com.example.learnbit.launch.teacher.profile.withdraw;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.example.learnbit.launch.teacher.profile.withdraw.model.Withdraw;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WithdrawActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner bankSpinner;
    private EditText accountNumberET, accountNameET, passwordET, amountET;
    private Button sendButton;
    private TextView withdrawBalance;

    private String spinnerValue;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseDatabase firebaseDatabase1;
    private DatabaseReference databaseReference1;

    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        bankSpinner = (Spinner) findViewById(R.id.withdraw_BankSpinner);
        accountNumberET = (EditText) findViewById(R.id.withdraw_AccountNumberET);
        accountNameET = (EditText) findViewById(R.id.withdraw_HolderName);
        passwordET = (EditText) findViewById(R.id.withdraw_PasswordET);
        amountET = (EditText) findViewById(R.id.withdraw_AmountET);
        withdrawBalance = (TextView) findViewById(R.id.withdraw_Balance);
        sendButton = (Button) findViewById(R.id.withdraw_SendButton);
        sendButton.setOnClickListener(this);

        setupSpinner();
        setupToolbar();
        setupFirebase();
        getData();
    }

    private void setupToolbar(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Send Balance");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bank_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bankSpinner.setAdapter(adapter);
        bankSpinner.setOnItemSelectedListener(this);
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        key = firebaseDatabase.getReference("Withdraw").push().getKey();
        databaseReference = firebaseDatabase.getReference("Withdraw").child(key);

        firebaseDatabase1 = FirebaseDatabase.getInstance();
        databaseReference1 = firebaseDatabase1.getReference("Users").child(user.getUid()).child("teacher").child("balance");
    }

    private void savetoDatabase(){
        if (user!=null){
            final String email = user.getEmail();
            final String password = passwordET.getText().toString();

            AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);

            user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        int accountNumber = 0;
                        int amount = 0;

                        try {
                            accountNumber = Integer.parseInt(accountNumberET.getText().toString());
                        }catch (NumberFormatException e){
                            Log.d("exception", "not a number");
                        }

                        try {
                            amount = Integer.parseInt(amountET.getText().toString());
                        }catch (NumberFormatException e){
                            Log.d("exception", "not a number");
                        }
                        
                        databaseReference.setValue(new Withdraw(spinnerValue, accountNumber, accountNameET.getText().toString(), false, amount, user.getUid()));
                        Toast.makeText(WithdrawActivity.this, "The withdraw process will take 2 - 3 days to be finished as we need to authenticate", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(WithdrawActivity.this, "Your entered password didn't match your account password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getData(){
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long balance = dataSnapshot.getValue(Long.class);

                withdrawBalance.setText("IDR " + balance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateBalance(){
        long newBalance = Long.parseLong(withdrawBalance.getText().toString().replace("IDR ", "")) - Long.parseLong(amountET.getText().toString());

        databaseReference1.setValue(newBalance);
    }

    private void checkEditText(){
        if (accountNameET.getText().toString().isEmpty()){
            accountNameET.setError("Bank Holder name shouldn't be empty");
        }else if (accountNumberET.getText().toString().isEmpty()){
            accountNumberET.setError("Bank Number shouldn't be empty");
        }else{
            savetoDatabase();
            updateBalance();
            Intent intent = new Intent(getApplicationContext(), TeacherMainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerValue = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.withdraw_SendButton) {
            checkEditText();
        } else {
            Toast.makeText(this, "nothing happened", Toast.LENGTH_SHORT).show();
        }
    }
}
