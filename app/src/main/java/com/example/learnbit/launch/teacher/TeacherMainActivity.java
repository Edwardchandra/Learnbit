package com.example.learnbit.launch.teacher;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.extension.BaseActivity;
import com.example.learnbit.launch.extension.ReminderBroadcast;
import com.example.learnbit.launch.extension.RemoveSinchService;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.teacher.home.TeacherHomeFragment;
import com.example.learnbit.launch.teacher.profile.TeacherProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TeacherMainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, RemoveSinchService {

    //initiate variables
    //timeArrayList is used for notification service
    private ArrayList<Long> timeArrayList = new ArrayList<>();

    //initiate permissions array
    private String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    //initiate notification id and permission code variable
    private static final String notificationChannelId = "REMINDER_1";
    private static final int PERMISSIONS_CODE = 1;

    //onCreate method run when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        //initiate elements in layout file and set navigation listener to override method
        BottomNavigationView bottomNavigationView = findViewById(R.id.teacherMain_BottomNavigationBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //load fragment into view when activity is launched
        loadFragment(new TeacherHomeFragment());

        createNotification();
        retrieveData();
        setupStatusBar();
    }

    //Change the color of status bar to match the color of background
    private void setupStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    //Load fragment view into container
    //container is in a framelayout form
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.teacherMain_FrameLayoutContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    //assign action to bottom navigation bar button when is clicked
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()){
            case R.id.home_menu:
                fragment = new TeacherHomeFragment();
                break;
            case R.id.profile_menu:
                fragment = new TeacherProfileFragment();
                break;
        }
        return loadFragment(fragment);
    }

    //create notification from retrieved time array
    private void createNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "ClassReminder";
            String description = "ClassReminderChannel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(notificationChannelId, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager!=null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    //retrieve data from firebase database
    private void retrieveData(){

        //initiate firebase variable and instance
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        //get the current user from firebase auth instance
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //get the path to the data in firebase database
        DatabaseReference databaseReference = firebaseDatabase.getReference("Course");

        //check if user is null or not
        //if user is null then terminate
        //if user is not null then proceed
        if (user!=null){

            //order the query by course acceptance
            //if course acceptance is equal to ACCEPTED then retrieve
            //if not, then the data won't be retrieved
            Query query = databaseReference.child(user.getUid()).orderByChild("courseAcceptance").equalTo("accepted");

            //initiate value event listener variable
            //value event listener is used to retrieve data from database
            //value event listener will be executed if there's a change in retrieved value from database
            ValueEventListener acceptedCourseEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //loop through children to retrieve all children data
                    for (DataSnapshot ds : dataSnapshot.getChildren()){

                        //retrieved value is stored as an object
                        Course course = ds.getValue(Course.class);

                        //check if retrieved data is null or not
                        //if retrieved data is null then terminate
                        //if retrieved data is not null then proceed
                        if (course!=null){

                            //get the current date time in day format(ie. Monday, Tuesday, Wednesday, etc)
                            //date format is used to convert the original date time format into the desired format
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                            String today = simpleDateFormat.format(Calendar.getInstance().getTime());

                            //loop through the schedule data in Course object
                            for (HashMap.Entry<String, String> scheduleEntry : course.getCourseSchedule().entrySet()){

                                //check if value of hashmap is equals to today day
                                //if equal then proceed
                                //if not then terminate
                                if (scheduleEntry.getValue().equals(today)){

                                    //loop through the time data in Course object
                                    for (HashMap.Entry<String,Boolean> entry : course.getCourseTime().entrySet()) {

                                        //initiate date time formatter
                                        SimpleDateFormat hourDateFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
                                        Date currDate = null;

                                        //convert course time string into date
                                        //catch error
                                        try {
                                            currDate = hourDateFormat.parse(entry.getKey());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        //check if time is null or not
                                        if (currDate!=null){

                                            //convert date into millis
                                            long millis = currDate.getTime();

                                            //add millis value to time arraylist
                                            timeArrayList.add(millis);
                                        }
                                    }
                                }
                            }

                            //if looping is finished, execute start alarm method
                            startAlarm();
                        }else {
                            toast(getString(R.string.retrieve_failed));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    toast(getString(R.string.retrieve_failed));
                }
            };


            query.addValueEventListener(acceptedCourseEventListener);
        }
    }

    //start notification alarm to alert about today course time schedule
    private void startAlarm() {
        Intent intent = new Intent(this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long currentTime = System.currentTimeMillis();

        for (int i=0;i<timeArrayList.size();i++){
            if (currentTime <= timeArrayList.get(i)){
                if (alarmManager!=null){
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeArrayList.get(i), pendingIntent);
                }
            }
        }
    }

    //when activity is started, execute
    @Override
    protected void onStart() {
        super.onStart();

        //check for permissions
        //if permission is not granted then ask for permission
        //if granted then terminate
        if (!checkPermission(getApplicationContext(), permissions)){
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_CODE);
        }
    }

    //check permissions method
    //require context and array of permissions parameters
    public static boolean checkPermission(Context context, String[] permissions){
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //check result of permission asked
    //if permission granted is more than one, permission is granted, show toast
    //if permission granted is 0 then permission is declined, show toast
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_CODE){
            if (grantResults.length > 0){
                toast(getString(R.string.permission_granted));
            }else{
                toast(getString(R.string.permission_declined));
            }
        }
    }

    //on back pressed exit application
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
    }

    //stop sinch client service when application is being destroyed by android
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (getSinchServiceInterface() != null){
            getSinchServiceInterface().stopClient();
        }
    }

    //method to stop sinch client service
    @Override
    public void removeSinchService() {
        if (getSinchServiceInterface()!=null){
            getSinchServiceInterface().stopClient();
        }
    }

    //method to execute toast
    private void toast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
