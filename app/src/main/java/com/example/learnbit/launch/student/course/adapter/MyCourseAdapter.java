package com.example.learnbit.launch.student.course.adapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.course.mycoursedetail.MyCourseDetailActivity;
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

import static android.content.Context.MODE_PRIVATE;

public class MyCourseAdapter extends RecyclerView.Adapter<MyCourseAdapter.MyCourseViewHolder> {

    private ArrayList<Course> courseArrayList;

    public MyCourseAdapter(ArrayList<Course> courseArrayList) {
        this.courseArrayList = courseArrayList;
    }

    @NonNull
    @Override
    public MyCourseAdapter.MyCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_student_course, parent, false);

        return new MyCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCourseAdapter.MyCourseViewHolder holder, int position) {
        holder.courseKey = courseArrayList.get(position).getCourseKey();
        holder.courseName.setText(courseArrayList.get(position).getCourseName());
        Glide.with(holder.itemView.getContext()).load(courseArrayList.get(position).getCourseImageURL()).into(holder.courseImageView);

        FirebaseDatabase.getInstance().getReference("Users").child(courseArrayList.get(position).getTeacherUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String teacherName = dataSnapshot.getValue(String.class);
                if (teacherName!=null) holder.courseTeacher.setText(teacherName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                holder.toast(holder.itemView.getContext().getString(R.string.retrieve_failed));
            }
        });

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        String today = simpleDateFormat.format(Calendar.getInstance().getTime());

        String startDateString = "", endDateString = "";
        Date startDate = new Date(), endDate = new Date();
        Date date = Calendar.getInstance().getTime();

        for (HashMap.Entry<String, String> courseDate : courseArrayList.get(position).getCourseDate().entrySet()){
            if (courseDate.getKey().equals("startDate")){
                startDateString = courseDate.getValue();
            }else if (courseDate.getKey().equals("endDate")){
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
            for (HashMap.Entry<String, String> entry : courseArrayList.get(position).getCourseSchedule().entrySet()){
                String schedule = entry.getValue();

                if (schedule.equals(today)){

                    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);

                    FirebaseDatabase.getInstance().getReference("Users").child(courseArrayList.get(position).getTeacherUid()).child("student").child("courses").child(courseArrayList.get(position).getCourseKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String time = dataSnapshot.getValue(String.class);
                            if (time!=null){
                                Date courseTime = new Date();
                                try {
                                    courseTime = simpleDateFormat2.parse(time);
                                }catch (ParseException e){
                                    e.printStackTrace();
                                }

                                if (!date.after(courseTime)){
                                    holder.courseSchedule.setText(holder.itemView.getContext().getString(R.string.course_start_time, time));
                                }else{
                                    holder.courseSchedule.setText(holder.itemView.getContext().getString(R.string.no_course));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            holder.toast(holder.itemView.getContext().getString(R.string.retrieve_failed));
                        }
                    });
                }else{
                    holder.courseSchedule.setText(holder.itemView.getContext().getString(R.string.no_course));
                }
            }
        }else if (date.after(endDate)){
            holder.courseSchedule.setText(holder.itemView.getContext().getString(R.string.course_end_period));
        }else if (date.before(startDate)){
            holder.courseSchedule.setText(holder.itemView.getContext().getString(R.string.course_start_period));
        }

        holder.itemView.setOnClickListener((v) -> {
            holder.savePreferenceData();
            Intent intent = new Intent(holder.itemView.getContext(), MyCourseDetailActivity.class);
            intent.putExtra("key", courseArrayList.get(position).getCourseKey());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (courseArrayList == null) ? 0 : courseArrayList.size();
    }

    public static class MyCourseViewHolder extends RecyclerView.ViewHolder {

        private ImageView courseImageView;
        private TextView courseName, courseTeacher, courseSchedule;
        private String courseKey;
        private static final String detailPreference = "DETAIL_PREFERENCE";

        public MyCourseViewHolder(@NonNull View itemView) {
            super(itemView);

            courseImageView = itemView.findViewById(R.id.myCourseImage);
            courseName = itemView.findViewById(R.id.myCourseName);
            courseTeacher = itemView.findViewById(R.id.myCourseTeacher);
            courseSchedule = itemView.findViewById(R.id.myCourseSchedule);

            courseImageView.setClipToOutline(true);

            savePreferenceData();
        }

        private void toast(String message){
            Toast.makeText(itemView.getContext(), message, Toast.LENGTH_SHORT).show();
        }

        private void savePreferenceData(){
            SharedPreferences preferences = itemView.getContext().getSharedPreferences(detailPreference, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("courseKey", courseKey);
            editor.apply();
        }
    }
}
