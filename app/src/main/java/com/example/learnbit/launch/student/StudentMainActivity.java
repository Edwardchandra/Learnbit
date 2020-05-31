package com.example.learnbit.launch.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.learnbit.R;
import com.example.learnbit.launch.student.course.StudentCourseFragment;
import com.example.learnbit.launch.student.home.StudentHomeFragment;
import com.example.learnbit.launch.student.profile.StudentProfileFragment;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.example.learnbit.launch.teacher.home.TeacherHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StudentMainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        loadFragment(new StudentHomeFragment());

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
        switch (menuItem.getItemId()){
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        moveTaskToBack(true);
    }
}
