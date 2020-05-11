package com.example.learnbit.launch.teacher;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.TeacherHomeFragment;
import com.example.learnbit.launch.teacher.profile.TeacherProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TeacherMainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

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
}
