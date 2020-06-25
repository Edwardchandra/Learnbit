package com.example.learnbit.launch.teacher.profile.withdraw;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
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
import com.example.learnbit.launch.teacher.profile.withdraw.history.WithdrawHistoryActivity;
import com.example.learnbit.launch.teacher.profile.withdraw.model.Withdraw;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class WithdrawActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner bankSpinner;
    private EditText accountNumberET, accountNameET, passwordET, amountET;
    private TextView withdrawBalance;

    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference1;

    private String spinnerValue;
    private String dateTime;
    private long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        bankSpinner = findViewById(R.id.withdraw_BankSpinner);
        accountNumberET = findViewById(R.id.withdraw_AccountNumberET);
        accountNameET = findViewById(R.id.withdraw_HolderName);
        passwordET = findViewById(R.id.withdraw_PasswordET);
        amountET = findViewById(R.id.withdraw_AmountET);
        withdrawBalance = findViewById(R.id.withdraw_Balance);
        Button sendButton = findViewById(R.id.withdraw_SendButton);
        sendButton.setOnClickListener(this);

        setupSpinner();
        setupToolbar();
        setupFirebase();
        getData();
        getCurrentDateTime();
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
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String key = firebaseDatabase.getReference("Withdraw").push().getKey();
        if (key!=null){
            databaseReference = firebaseDatabase.getReference("Withdraw").child(key);
        }

        FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
        databaseReference1 = firebaseDatabase1.getReference("Users").child(user.getUid()).child("teacher").child("balance");
    }

    private void savetoDatabase(){
        if (user!=null){
            final String email = user.getEmail();
            final String password = passwordET.getText().toString();

            if (email!=null){
                AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);

                user.reauthenticate(authCredential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        int accountNumber = 0;
                        int amount = 0;

                        try {
                            accountNumber = Integer.parseInt(accountNumberET.getText().toString());
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }

                        try {
                            amount = Integer.parseInt(amountET.getText().toString());
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }

                        databaseReference.setValue(new Withdraw(spinnerValue, accountNumber, accountNameET.getText().toString(), "pending", amount, user.getUid(), dateTime, timestamp));
                        toast(getString(R.string.withdraw_process));
                    }else{
                        toast(getString(R.string.password_not_match));
                    }
                });
            }
        }
    }

    private void getCurrentDateTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("WIB"));

        dateTime = simpleDateFormat.format(new java.util.Date());

        timestamp = System.currentTimeMillis()/1000;
    }

    private void getData(){
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long balance = dataSnapshot.getValue(Long.class);

                if (balance!=null){
                    withdrawBalance.setText(getString(R.string.price, balance));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    private void updateBalance(){
        long newBalance = Long.parseLong(withdrawBalance.getText().toString().replace("IDR ", "")) - Long.parseLong(amountET.getText().toString());

        databaseReference1.setValue(newBalance);
    }

    private void checkEditText(){
        if(amountET.getText().toString().isEmpty()){
            amountET.setError(getString(R.string.withdraw_amount_error));
        }else if(passwordET.getText().toString().isEmpty()){
            passwordET.setError(getString(R.string.password_field_error));
        }else if(passwordET.getText().toString().length() < 7){
            passwordET.setError(getString(R.string.password_field_error_character));
        }else if (accountNameET.getText().toString().isEmpty()){
            accountNameET.setError(getString(R.string.bank_holder_error));
        }else if (accountNumberET.getText().toString().isEmpty()){
            accountNumberET.setError(getString(R.string.bank_number_error));
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
            finish();
        } else if(item.getItemId() == R.id.withdraw_menu){
            Intent intent = new Intent(this, WithdrawHistoryActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.withdraw_SendButton) {
            checkEditText();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_withdraw_menu, menu);
        return true;
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
