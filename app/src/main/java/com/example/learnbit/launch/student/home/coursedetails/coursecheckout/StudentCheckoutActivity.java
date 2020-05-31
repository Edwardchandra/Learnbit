package com.example.learnbit.launch.student.home.coursedetails.coursecheckout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.extension.PaymentUtils;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.model.userdata.Notifications;
import com.example.learnbit.launch.student.StudentMainActivity;
import com.example.learnbit.launch.student.home.coursedetails.coursecheckout.adapter.TermsAdapter;
import com.example.learnbit.launch.teacher.home.addcourse.fourthsection.model.Terms;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class StudentCheckoutActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Toolbar toolbar;
    private TextView courseNameET, courseCategoryET;
    private ImageView courseImageView;
    private RecyclerView termsRV;
    private CheckBox termsCheckBox;
    private RelativeLayout checkoutButton;

    private String spinnerValue, price, courseName, key;

    private ArrayList<String> timeArrayList = new ArrayList<>();
    private ArrayList<String> termsArrayList = new ArrayList<>();

    private ArrayAdapter<String> arrayAdapter;

    private PaymentsClient paymentsClient;
    private PaymentUtils paymentUtils;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private int courseCounter = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_checkout);

        toolbar = findViewById(R.id.checkout_Toolbar);
        courseNameET = findViewById(R.id.checkout_CourseTitle);
        courseCategoryET = findViewById(R.id.checkout_CourseCategory);
        Spinner timeSpinner = findViewById(R.id.checkout_CourseTimeSpinner);
        checkoutButton = findViewById(R.id.checkout_CheckoutButton);
        termsRV = findViewById(R.id.checkout_TermsConditionsRecyclerView);
        termsCheckBox = findViewById(R.id.checkout_TermsConditionsCheckbox);
        courseImageView = findViewById(R.id.checkout_ImageView);

        setupToolbar();
        setupFirebase();
        setupSpinner(timeSpinner);
        retrieveData();
        addData();
        setupRecyclerView();
        
        checkoutButton.setOnClickListener(this);
        checkoutButton.setVisibility(View.INVISIBLE);

        // Initialize a Google Pay API client for an environment suitable for testing.
        // It's recommended to create the PaymentsClient object inside of the onCreate method.
        paymentsClient = PaymentUtils.createPaymentsClient(this);
        possiblyShowGooglePayButton();
    }

    private void setupRecyclerView(){
        TermsAdapter termsAdapter = new TermsAdapter(termsArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        termsRV.setLayoutManager(layoutManager);
        termsRV.setAdapter(termsAdapter);
    }

    private void setupFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();
    }

    private void retrieveData() {
        courseName = getIntent().getStringExtra("courseName");
        key = getIntent().getStringExtra("key");
        price = getIntent().getStringExtra("price");

        if (key != null && courseName != null) {
            Query query = firebaseDatabase.getReference("Course").child(key).orderByChild("courseName").equalTo(courseName);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Course course = ds.getValue(Course.class);

                        if (course != null) {
                            courseNameET.setText(course.getCourseName());

                            courseCategoryET.setText(course.getCourseCategory());

                            Glide.with(getApplicationContext()).load(course.getCourseImageURL()).into(courseImageView);

                            HashMap<String, Boolean> courseTime = course.getCourseTime();

                            for (String key : courseTime.keySet()){
                                Boolean value = courseTime.get(key);

                                if (value!=null){
                                    if (!value){
                                        timeArrayList.add(key);
                                        arrayAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        firebaseDatabase.getReference("Users").child(user.getUid()).child("student").child("courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                courseCounter = (int) dataSnapshot.getChildrenCount();

                Log.d("counter", courseCounter + " ");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Course Checkout");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupSpinner(Spinner spinner){
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkout_CheckoutButton){
            if (!termsCheckBox.isChecked()){
                Toast.makeText(this, "Please read and agree with the terms and conditions before you checkout.", Toast.LENGTH_SHORT).show();
            }else{
                requestPayment(v);
            }
        }
    }

    private void setData(){
        DatabaseReference databaseReference = firebaseDatabase.getReference("Course").child(key);
        Query query = databaseReference.orderByChild("courseName").equalTo(courseName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String courseKey = "";
                int studentCount = 0;

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    courseKey = ds.getKey();
                    Course course = ds.getValue(Course.class);

                    if (course!=null){
                        if (course.getCourseStudent()!=null){
                            studentCount = course.getCourseStudent().size();
                            Log.d("studentCount", course.getCourseStudent().size() + " ");
                        }else{
                            studentCount = 0;
                        }
                    }
                }

                if (courseKey!=null){
                    firebaseDatabase.getReference("Users").child(user.getUid()).child("student").child("courses").child("course " + (courseCounter + 1)).setValue(courseKey);
                    firebaseDatabase.getReference("Course").child(key).child(courseKey).child("courseStudent").child("student " + (studentCount + 1)).setValue(user.getUid());
                    firebaseDatabase.getReference("Course").child(key).child(courseKey).child("courseTime").child(spinnerValue).setValue(true);

                    Toast.makeText(StudentCheckoutActivity.this, "You have successfully applied to this course.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), StudentMainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;

        if (spinner.getId() == R.id.checkout_CourseTimeSpinner) {
            spinnerValue = parent.getItemAtPosition(position).toString();
            TextView selectedValue = (TextView) parent.getChildAt(0);
            if (selectedValue != null) {
                selectedValue.setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private void addData(){
        termsArrayList.add("You will be given 3 absent quota. If you pass the given quota, you will not be able to get the certificate of the course.");
        termsArrayList.add("You can cancel the course anytime. By cancelling, your previous payment will not be returned and you will not get your certificate.");
        termsArrayList.add("A notification will appear at the time the course is going to start. If you exceed 15 minutes after the course started then it will be counted as absent.");
        termsArrayList.add("Your teacher may able to cancel your course if your teacher find you guilty.");
        termsArrayList.add("In the last day of the course, there will be 2 quizzes. Your teacher will be able to monitor you and see if you have committ guilt or not.");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void possiblyShowGooglePayButton() {

        final Optional<JSONObject> isReadyToPayJson = PaymentUtils.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(this,
                task1 -> {
                    if (task1.isSuccessful()) {
                        checkoutButton.setVisibility(View.VISIBLE);
                    } else {
                        Log.w("isReadyToPay failed", task1.getException());
                    }
                });
    }

    private void handlePaymentSuccess(PaymentData paymentData) {

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        final String paymentInfo = paymentData.toJson();
        if (paymentInfo == null) {
            return;
        }

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
            final String tokenizationType = tokenizationData.getString("type");
            final String token = tokenizationData.getString("token");

            if ("PAYMENT_GATEWAY".equals(tokenizationType) && "examplePaymentMethodToken".equals(token)) {
                new AlertDialog.Builder(this)
                        .setTitle("Payment Successfull")
                        .setMessage("Course has been purchased and added to your course list. Enjoy your live learning with the teacher.")
                        .setPositiveButton("OK", null)
                        .create()
                        .show();
            }

            final JSONObject info = paymentMethodData.getJSONObject("info");
            final String billingName = info.getJSONObject("billingAddress").getString("name");
            Toast.makeText(
                    this, billingName + " ",
                    Toast.LENGTH_LONG).show();

            // Logging token string.
            Log.d("Google Pay token: ", token);

        } catch (JSONException e) {
            throw new RuntimeException("The selected garment cannot be parsed from the list of elements");
        }
    }

    private void handleError(int statusCode) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestPayment(View view) {

        // Disables the button to prevent multiple clicks.
        checkoutButton.setClickable(false);

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        String price = getIntent().getStringExtra("price").replace("IDR ", "");

        long priceValue = Long.parseLong(price);

        Optional<JSONObject> paymentDataRequestJson = PaymentUtils.getPaymentDataRequest(priceValue);
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }

        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request),
                    this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {

                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        setData();
                        break;

                    case Activity.RESULT_CANCELED:
                        // The user cancelled the payment attempt
                        break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleError(status.getStatusCode());
                        break;
                }

                // Re-enables the Google Pay payment button.
                checkoutButton.setClickable(true);
        }
    }
}
