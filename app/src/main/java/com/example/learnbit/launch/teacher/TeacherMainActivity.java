package com.example.learnbit.launch.teacher;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.learnbit.R;
import com.example.learnbit.launch.extension.ReminderBroadcast;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.teacher.home.TeacherHomeFragment;
import com.example.learnbit.launch.teacher.home.addcourse.secondsection.model.Time;
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

public class TeacherMainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ArrayList<Long> timeArrayList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        createNotification();
        retrieveData();


        loadFragment(new TeacherHomeFragment());

        BottomNavigationView bottomNavigationView = findViewById(R.id.teacherMain_BottomNavigationBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.teacherMain_FrameLayoutContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }

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

    private void createNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "ClassReminder";
            String description = "ClassReminderChannel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel("reminder01", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager!=null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void retrieveData(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Course");

        if (user!=null){
            Query query = databaseReference.child(user.getUid()).orderByChild("courseAcceptance").equalTo("accepted");

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        Course course = ds.getValue(Course.class);

                        if (course!=null){
                            HashMap<String, Boolean> courseTime = course.getCourseTime();
                            HashMap<String, String> courseSchedule = course.getCourseSchedule();
                            ArrayList<String> scheduleArrayList = new ArrayList<>();

                            Calendar calendar = Calendar.getInstance();
                            Date date = calendar.getTime();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                            String today = simpleDateFormat.format(date);

                            for (HashMap.Entry<String, String> scheduleEntry : courseSchedule.entrySet()){
                                String value = scheduleEntry.getValue();

                                scheduleArrayList.add(value);
                            }

                            for (int i=0;i<scheduleArrayList.size();i++){
                                if (scheduleArrayList.get(i).equals(today)){
                                    for (HashMap.Entry<String,Boolean> entry : courseTime.entrySet()) {
                                        String key = entry.getKey();

                                        SimpleDateFormat hourDateFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
                                        Date currDate = null;

                                        try {
                                            currDate = hourDateFormat.parse(key);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (currDate!=null){
                                            long millis = currDate.getTime();

                                            timeArrayList.add(millis);
                                        }
                                    }
                                }
                            }

                            Log.d("course time array", timeArrayList.size() + " ");

                            startAlarm();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
