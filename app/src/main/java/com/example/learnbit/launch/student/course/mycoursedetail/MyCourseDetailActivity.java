package com.example.learnbit.launch.student.course.mycoursedetail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.extension.BaseActivity;
import com.example.learnbit.launch.extension.SinchService;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.reusableactivity.CallScreenActivity;
import com.example.learnbit.launch.reusableactivity.ChatActivity;
import com.example.learnbit.launch.student.course.adapter.MyCourseSectionAdapter;
import com.example.learnbit.launch.student.course.mycoursedetail.rating.RatingActivity;
import com.example.learnbit.launch.student.course.mycoursedetail.terminate.TerminateActivity;
import com.example.learnbit.launch.student.teacherprofile.TeacherProfileActivity;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.adapter.SectionAdapter;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model.Section;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.sinch.android.rtc.calling.Call;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MyCourseDetailActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView sectionRecyclerView;
    private TextView myCourseName, myCourseCategory, myCourseStartTime, teacherName, teacherRatings, teacherCourseCount, scoreSummary, scoreDescription, score;
    private ImageView teacherImageView, myCourseImageView;
    private Toolbar myCourseToolbar;
    private Button callButton;
    private CardView scoreOverallCard;
    private LinearLayout scoreCard;

    private String courseTime;
    private String key;
    private String teacherUid;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;

    private MyCourseSectionAdapter sectionAdapter;
    private ArrayList<Section> sectionArrayList = new ArrayList<>();

    private static final String detailPreference = "DETAIL_PREFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course_detail);

        myCourseName = findViewById(R.id.myCourseDetail_CourseTitle);
        myCourseCategory = findViewById(R.id.myCourseDetail_CourseCategory);
        myCourseStartTime = findViewById(R.id.myCourseStartTime);
        teacherName = findViewById(R.id.teacherName);
        teacherRatings = findViewById(R.id.teacherRatings);
        teacherImageView = findViewById(R.id.teacherImageView);
        myCourseImageView = findViewById(R.id.myCourseDetail_ImageView);
        teacherCourseCount = findViewById(R.id.teacherCourseCount);
        callButton = findViewById(R.id.callButton);
        sectionRecyclerView = findViewById(R.id.curriculumRecyclerView);
        myCourseToolbar = findViewById(R.id.myCourseDetail_Toolbar);
        ConstraintLayout teacherProfile = findViewById(R.id.teacherProfile);
        scoreSummary = findViewById(R.id.scoreStatus);
        scoreDescription = findViewById(R.id.scoreDescription);
        score = findViewById(R.id.score);
        scoreOverallCard = findViewById(R.id.scoreOverallCard);
        scoreCard = findViewById(R.id.scoreCard);
        Button chatButton = findViewById(R.id.chatButton);

        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        });

        teacherProfile.setOnClickListener((v) -> {
            Intent intent = new Intent(this, TeacherProfileActivity.class);
            intent.putExtra("teacherUid", teacherUid);
            startActivity(intent);
        });

        callButton.setEnabled(false);
        callButton.setOnClickListener(this);

        teacherImageView.setClipToOutline(true);

        setupToolbar();
        retrieveIntentData();
        savePreferenceData();
        setupFirebase();
        setupRecyclerView();
        retrieveData();
        retrieveScoreData();
        checkServiceEnabled();
    }

    private void setupFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setupToolbar() {
        setSupportActionBar(myCourseToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Course Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void retrieveIntentData() {
        key = getIntent().getStringExtra("key");
    }

    private void retrieveData() {
        sectionArrayList.clear();

        DatabaseReference databaseReference = firebaseDatabase.getReference("Course").child(key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Course course = dataSnapshot.getValue(Course.class);

                if (course != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                    String today = simpleDateFormat.format(Calendar.getInstance().getTime());

                    String startDateString = "", endDateString = "";
                    Date startDate = new Date(), endDate = new Date();
                    Date date = Calendar.getInstance().getTime();

                    for (HashMap.Entry<String, String> courseDate : course.getCourseDate().entrySet()) {
                        if (courseDate.getKey().equals("startDate")) {
                            startDateString = courseDate.getValue();
                        } else if (courseDate.getKey().equals("endDate")) {
                            endDateString = courseDate.getValue();
                        }
                    }

                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);

                    try {
                        startDate = simpleDateFormat1.parse(startDateString);
                        endDate = simpleDateFormat1.parse(endDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (date.after(startDate) && date.before(endDate)) {
                        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);

                        FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("student").child("courses").child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String time = dataSnapshot.getValue(String.class);
                                if (time != null) {
                                    courseTime = time;
                                    if (!time.equalsIgnoreCase("terminate")) {
                                        for (HashMap.Entry<String, String> entry : course.getCourseSchedule().entrySet()) {
                                            String schedule = entry.getValue();

                                            if (schedule.equals(today)) {
                                                Date courseTime = new Date();
                                                try {
                                                    courseTime = simpleDateFormat2.parse(time);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                                if (!date.after(courseTime)) {
                                                    myCourseStartTime.setText(getString(R.string.course_start_time, time));
                                                } else {
                                                    myCourseStartTime.setText(getString(R.string.no_course));
                                                }
                                            } else {
                                                myCourseStartTime.setText(getString(R.string.no_course));
                                            }
                                        }
                                    } else {
                                        myCourseStartTime.setText(getString(R.string.terminate_request));
                                        callButton.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                toast(getString(R.string.retrieve_failed));
                            }
                        });
                    } else if (date.before(startDate)) {
                        courseTime = "notstart";
                        myCourseStartTime.setText(getString(R.string.course_start_period));
                        callButton.setVisibility(View.GONE);
                    } else if (date.after(endDate)) {
                        courseTime = "hasended";
                        myCourseStartTime.setText(getString(R.string.course_end_period));
                        callButton.setText(getString(R.string.call_button_rating));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            callButton.setBackground(getDrawable(R.drawable.rating_button));
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        for (HashMap.Entry<String, Section> entry : course.getCourseCurriculum().entrySet()) {
                            sectionArrayList.add(new Section(entry.getKey(), entry.getValue().getName(), entry.getValue().getTopics()));
                            sectionArrayList.sort(Comparator.comparing(Section::getWeek));
                        }
                    }

                    teacherUid = course.getTeacherUid();
                    myCourseName.setText(course.getCourseName());
                    myCourseCategory.setText(getString(R.string.divider, course.getCourseCategory(), course.getCourseSubcategory()));
                    Glide.with(getApplicationContext()).load(course.getCourseImageURL()).into(myCourseImageView);

                    firebaseDatabase.getReference("Users").child(course.getTeacherUid()).child("name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.getValue(String.class);
                            if (name != null) {
                                teacherName.setText(name);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            toast(getString(R.string.retrieve_failed));
                        }
                    });

                    firebaseDatabase.getReference("Users").child(course.getTeacherUid()).child("teacher").child("rating").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Float rating = dataSnapshot.getValue(Float.class);
                            if (rating != null) {
                                teacherRatings.setText(getString(R.string.teacher_rating, rating));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            toast(getString(R.string.retrieve_failed));
                        }
                    });

                    firebaseDatabase.getReference("Course").orderByChild("teacherUid").equalTo(course.getTeacherUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long courseCount = dataSnapshot.getChildrenCount();
                            teacherCourseCount.setText(getString(R.string.course_count, courseCount));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            toast(getString(R.string.retrieve_failed));
                        }
                    });

                    FirebaseStorage.getInstance().getReference("Users").child(course.getTeacherUid()).child("profileimage").getDownloadUrl()
                            .addOnSuccessListener(uri -> Glide.with(getApplicationContext()).load(uri).into(teacherImageView))
                            .addOnFailureListener(e -> toast(getString(R.string.retrieve_failed)));

                    sectionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    private void retrieveScoreData(){
        FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("student").child("score").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int total = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Integer value = ds.getValue(Integer.class);
                    if (value!=null){
                        total = total + value;
                    }
                }

                if (total == 0){
                    scoreOverallCard.setVisibility(View.GONE);
                }else{
                    scoreOverallCard.setVisibility(View.VISIBLE);
                    score.setText(getString(R.string.score, total));
                    if (total < 50){
                        scoreCard.setBackgroundColor(getResources().getColor(R.color.redColor));
                        scoreSummary.setText(getString(R.string.score50));
                        scoreDescription.setText(getString(R.string.score50_decription));
                    }else if (total < 80){
                        scoreCard.setBackgroundColor(getResources().getColor(R.color.orangeColor));
                        scoreSummary.setText(getString(R.string.score80));
                        scoreDescription.setText(getString(R.string.score80_description));
                    }else {
                        scoreCard.setBackgroundColor(getResources().getColor(R.color.greenColor));
                        scoreSummary.setText(getString(R.string.score100));
                        scoreDescription.setText(getString(R.string.score100_description));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    private void setupRecyclerView() {
        sectionAdapter = new MyCourseSectionAdapter(sectionArrayList);
        RecyclerView.LayoutManager sectionLayoutManager = new LinearLayoutManager(this);
        sectionRecyclerView.setLayoutManager(sectionLayoutManager);
        sectionRecyclerView.setAdapter(sectionAdapter);
        sectionRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.callButton) {
            if (callButton.getText().toString().equals(getString(R.string.call_button_rating))) {
                Intent intent = new Intent(this, RatingActivity.class);
                intent.putExtra("courseKey", key);
                startActivity(intent);
            } else {
                callAction();
            }
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        callButton.setEnabled(true);
        checkServiceEnabled();
    }

    private void checkServiceEnabled() {
        if (!callButton.isEnabled()) {
            callButton.setClickable(false);
            callButton.setBackground(getResources().getDrawable(R.drawable.call_button_disabled));
        } else {
            callButton.setClickable(true);
            callButton.setBackground(getResources().getDrawable(R.drawable.call_button));
        }
    }

    private void callAction() {
        if (teacherUid != null) {
            Call call = getSinchServiceInterface().callUserVideo(teacherUid);
            String callID = call.getCallId();
            Intent callScreen = new Intent(this, CallScreenActivity.class);
            callScreen.putExtra(SinchService.CALL_ID, callID);
            startActivity(callScreen);
        }
    }

    //show toast
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void savePreferenceData() {
        SharedPreferences preferences = getSharedPreferences(detailPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("courseKey", key);
        editor.apply();
    }

    //inflate right dot menu to toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.student_course_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.terminate_menu:
                if (courseTime.equalsIgnoreCase("hasended")){
                    toast(getString(R.string.course_end_period));
                }else if (courseTime.equalsIgnoreCase("terminate")){
                    toast(getString(R.string.terminate_requested));
                } else{
                    Intent intent = new Intent(this, TerminateActivity.class);
                    intent.putExtra("courseKey", key);
                    intent.putExtra("courseTime", courseTime);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}