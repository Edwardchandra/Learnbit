package com.example.learnbit.launch.student;

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
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.extension.BaseActivity;
import com.example.learnbit.launch.extension.ReminderBroadcast;
import com.example.learnbit.launch.extension.RemoveSinchService;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.course.StudentCourseFragment;
import com.example.learnbit.launch.student.home.StudentHomeFragment;
import com.example.learnbit.launch.student.profile.StudentProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class StudentMainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, RemoveSinchService {

    //initiate permissions array
    private String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    //initiate notification id and permission code variable
    private static final String notificationChannelId = "REMINDER_1";
    private static final int PERMISSIONS_CODE = 1;

    private ArrayList<Long> timeArrayList = new ArrayList<>();

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        loadFragment(new StudentHomeFragment());
        setupBottomNavigationBar();
        setupFirebase();
        retrieveCourseData();
        createNotification();
    }

    private void setupBottomNavigationBar(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.studentMain_BottomNavigationBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.studentMain_FrameLayoutContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.student_home_menu:
                fragment = new StudentHomeFragment();
                break;
            case R.id.student_course_menu:
                fragment = new StudentCourseFragment();
                break;
            case R.id.student_profile_menu:
                fragment = new StudentProfileFragment();
                break;
        }
        return loadFragment(fragment);
    }

    private void setupFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void retrieveCourseData(){
        FirebaseDatabase.getInstance().getReference("Course").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Course course = ds.getValue(Course.class);
                    if (course!=null){
                        if (course.getCourseStudent()!=null){
                            for (HashMap.Entry<String, String> courseStudent : course.getCourseStudent().entrySet()){
                                if (courseStudent.getValue().equals(user.getUid())){

                                    String startDateString = "", endDateString = "";
                                    Date startDate = new Date(), endDate = new Date();
                                    Date date = Calendar.getInstance().getTime();

                                    for (HashMap.Entry<String, String> entry : course.getCourseDate().entrySet()){
                                        if (entry.getKey().equals("startDate")){
                                            startDateString = entry.getValue();
                                        }else if (entry.getKey().equals("endDate")){
                                            endDateString = entry.getValue();
                                        }
                                    }

                                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);

                                    try {
                                        startDate = simpleDateFormat1.parse(startDateString);
                                        endDate = simpleDateFormat1.parse(endDateString);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    if (date.after(startDate) && date.before(endDate)){
                                        //get the current date time in day format(ie. Monday, Tuesday, Wednesday, etc)
                                        //date format is used to convert the original date time format into the desired format
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                                        String today = simpleDateFormat.format(Calendar.getInstance().getTime());

                                        //loop through the schedule data in Course object
                                        for (HashMap.Entry<String, String> scheduleEntry : course.getCourseSchedule().entrySet()) {

                                            //check if value of hashmap is equals to today day
                                            //if equal then proceed
                                            //if not then terminate
                                            if (scheduleEntry.getValue().equals(today)) {
                                                FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("student").child("courses").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                                                            String courseTime = ds.getValue(String.class);
                                                            if (courseTime!=null){
                                                                if (!courseTime.equals("terminate")){
                                                                    //initiate date time formatter
                                                                    SimpleDateFormat hourDateFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
                                                                    Date currDate = null;

                                                                    //convert course time string into date
                                                                    //catch error
                                                                    try {
                                                                        currDate = hourDateFormat.parse(courseTime);
                                                                    } catch (ParseException e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                    //check if time is null or not
                                                                    if (currDate != null) {

                                                                        //convert date into millis
                                                                        long millis = currDate.getTime();

                                                                        //add millis value to time arraylist
                                                                        timeArrayList.add(millis);
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        startAlarm();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        toast(getString(R.string.retrieve_failed));
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (getSinchServiceInterface() != null){
            getSinchServiceInterface().stopClient();
        }
    }

    @Override
    public void removeSinchService() {
        if (getSinchServiceInterface()!=null){
            getSinchServiceInterface().stopClient();
        }
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
