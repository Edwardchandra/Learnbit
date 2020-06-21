package com.example.learnbit.launch.teacher.home.search.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.teacher.home.coursedetail.CourseDetailActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class TeacherSearchAdapter extends RecyclerView.Adapter<TeacherSearchAdapter.TeacherSearchViewHolder> {

    //initiate variables
    private ArrayList<Course> courseArrayList;

    //initiate preference key variable
    private static final String detailPreference = "DETAIL_PREFERENCE";

    //constructor
    public TeacherSearchAdapter(ArrayList<Course> courseArrayList) {
        this.courseArrayList = courseArrayList;
    }

    //inflate each recycler view cell with layout
    @NonNull
    @Override
    public TeacherSearchAdapter.TeacherSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_teacher_course_list, parent, false);

        return new TeacherSearchAdapter.TeacherSearchViewHolder(view);
    }

    //set layout elements in each cell with values
    @Override
    public void onBindViewHolder(@NonNull TeacherSearchAdapter.TeacherSearchViewHolder holder, int position) {

        //set course name
        holder.teacherCourseName.setText(courseArrayList.get(position).getCourseName());

        //set course image
        Glide.with(holder.context).load(courseArrayList.get(position).getCourseImageURL()).into(holder.teacherCourseImageView);

        //get current time
        Date currentTime = new Date();

        //initiate new date
        Date startDate = new Date();
        Date endDate = new Date();

        //date formatter
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);

        //simple date format is used to format date to the desired format
        //this formatter convert date to "HOUR:MINUTES [AM/PM]" format
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);

        for (HashMap.Entry<String, String> entry : courseArrayList.get(position).getCourseDate().entrySet()){
            if (entry.getKey().equals("startDate")){
                try {
                    startDate = simpleDateFormat1.parse(entry.getValue());
                }catch (ParseException e){
                    e.printStackTrace();
                }
            }else if (entry.getKey().equals("endDate")){
                try {
                    endDate = simpleDateFormat1.parse(entry.getValue());
                }catch (ParseException e){
                    e.printStackTrace();
                }
            }
        }

        //check if course is accepted/not
        //if course is still pending, set course time textfield to "awaiting admin confirmation"
        //if course is accepted, show next schedule for today
        if (courseArrayList.get(position).getCourseAcceptance().equals("pending")){
            holder.teacherCourseTime.setText(holder.context.getString(R.string.awaiting_admin_confirmation));
        }else {
            if (currentTime.after(startDate) && currentTime.before(endDate)){
                //loop through course time for next course time
                for (HashMap.Entry<String, Boolean> entry : courseArrayList.get(position).getCourseTime().entrySet()){

                    //check if course time is being taken
                    //if no time is taken, set to no schedule today
                    if (entry.getValue()) {
                        //initiate current date
                        Date courseTime = new Date();

                        //parse current date using date formatter
                        //catch error if parse failed
                        try {
                            courseTime = simpleDateFormat.parse(entry.getKey());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //check if current time is not yet passing course time
                        //if not yet pass the course time, then set to course time textfield
                        //if passed then see next course time, if no course time ahead then set to no schedule today
                        if (!currentTime.after(courseTime)) {
                            holder.teacherCourseTime.setText(holder.context.getString(R.string.course_start_time, entry.getKey()));
                        } else {
                            holder.teacherCourseTime.setText(holder.context.getString(R.string.no_course));
                        }
                    }else{
                        holder.teacherCourseTime.setText(holder.context.getString(R.string.no_course));
                    }
                }
            }else if (!currentTime.after(startDate) && currentTime.before(endDate)){
                holder.teacherCourseTime.setText(holder.context.getString(R.string.course_start_period));
            }else if (currentTime.after(startDate) && !currentTime.before(endDate)){
                holder.teacherCourseTime.setText(holder.context.getString(R.string.course_end_period));
            }
        }

        //if course student is not null, set course student count field to total applied student
        //if null, set to no student yet
        if (courseArrayList.get(position).getCourseStudent() != null){
            holder.teacherCourseStudentCount.setText(holder.context.getString(R.string.course_student_count, courseArrayList.get(position).getCourseStudent().size()));
        }else{
            holder.teacherCourseStudentCount.setText(holder.context.getString(R.string.no_course_student));
        }

        holder.itemView.setOnClickListener(v -> {
            SharedPreferences preferences = holder.context.getSharedPreferences(detailPreference, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("courseKey", courseArrayList.get(position).getCourseKey());
            editor.apply();

            Intent intent = new Intent(holder.context, CourseDetailActivity.class);
            intent.putExtra("courseName", courseArrayList.get(position).getCourseName());
            intent.putExtra("key", courseArrayList.get(position).getCourseKey());
            holder.context.startActivity(intent);
        });
    }

    //set recyclerview cell count
    //check if arraylists is null or not
    //if null return 0 cell
    //if not return arraylists size
    @Override
    public int getItemCount() {
        return (courseArrayList == null) ? 0 : courseArrayList.size();
    }

    //update arraylist on search bar text update
    public void updateList(ArrayList<Course> courseArrayList){
        this.courseArrayList = courseArrayList;
        notifyDataSetChanged();
    }

    //recycler view holder class, used to initiate element variables and any other things regarding elements
    public static class TeacherSearchViewHolder extends RecyclerView.ViewHolder {

        //initiate element variables
        private ImageView teacherCourseImageView;
        private TextView teacherCourseName, teacherCourseStudentCount, teacherCourseTime;
        private Context context;

        //method executed here
        public TeacherSearchViewHolder(@NonNull View itemView) {
            super(itemView);

            teacherCourseImageView = itemView.findViewById(R.id.teacherCourseImageView);
            teacherCourseName = itemView.findViewById(R.id.teacherCourseNameTV);
            teacherCourseStudentCount = itemView.findViewById(R.id.teacherCourseStudentCountTV);
            teacherCourseTime = itemView.findViewById(R.id.teacherCourseTimeTV);

            teacherCourseImageView.setClipToOutline(true);

            context = itemView.getContext();
        }
    }
}
