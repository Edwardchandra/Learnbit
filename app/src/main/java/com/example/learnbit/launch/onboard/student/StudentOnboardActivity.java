package com.example.learnbit.launch.onboard.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.example.learnbit.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class StudentOnboardActivity extends AppCompatActivity {

    private ViewPager onboardViewpager;
    TabLayout onboardTabLayout;
    Button onboardNextButton, onboardSkipButton, onboardGetStartedButton;

    int position = 0;
    StudentOnboardViewpagerAdapter studentOnboardViewpagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_onboard);

        final List<StudentOnboardElements> mList = new ArrayList<>();
        mList.add(new StudentOnboardElements(getString(R.string.student_onboard_title_a), getString(R.string.student_onboard_subtitle_a), R.drawable.onboard_a));
        mList.add(new StudentOnboardElements(getString(R.string.student_onboard_title_b), getString(R.string.student_onboard_subtitle_b), R.drawable.onboard_b));
        mList.add(new StudentOnboardElements(getString(R.string.student_onboard_title_c), getString(R.string.student_onboard_subtitle_c), R.drawable.onboard_c));

        onboardViewpager = (ViewPager) findViewById(R.id.onboard_Viewpager);
        onboardTabLayout = (TabLayout) findViewById(R.id.onboard_TabLayout);
        onboardNextButton = (Button) findViewById(R.id.onboard_NextButton);
        onboardSkipButton = (Button) findViewById(R.id.onboard_SkipButton);
        onboardGetStartedButton = (Button) findViewById(R.id.onboard_StartButton);

        final Animation onboardStartButtonNextAnimation = AnimationUtils.loadAnimation(this, R.anim.onboard_button_next_animation);

        studentOnboardViewpagerAdapter = new StudentOnboardViewpagerAdapter(this, mList);
        onboardViewpager.setAdapter(studentOnboardViewpagerAdapter);

        onboardTabLayout.setupWithViewPager(onboardViewpager, true);

        onboardNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = onboardViewpager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    onboardViewpager.setCurrentItem(position);
                }

                if (position == mList.size() - 1) {
                    onboardGetStartedButton.setAnimation(onboardStartButtonNextAnimation);
                    loadStartButton();
                }

                if (position == 0 || position == 1) {
                    hideStartButton();
                }
            }
        });

        onboardTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size() - 1) {
                    onboardGetStartedButton.setAnimation(onboardStartButtonNextAnimation);
                    loadStartButton();
                }
                if (tab.getPosition() == 0 || tab.getPosition() == 1) {
                    hideStartButton();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private void loadStartButton() {
        onboardNextButton.setVisibility(View.INVISIBLE);
        onboardSkipButton.setVisibility(View.INVISIBLE);
        onboardGetStartedButton.setVisibility(View.VISIBLE);
    }

    private void hideStartButton(){
        onboardNextButton.setVisibility(View.VISIBLE);
        onboardSkipButton.setVisibility(View.VISIBLE);
        onboardGetStartedButton.setVisibility(View.INVISIBLE);
    }
}
