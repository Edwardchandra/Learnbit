package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.extension.SinchService;
import com.example.learnbit.launch.reusableactivity.CallScreenActivity;
import com.example.learnbit.launch.teacher.home.coursedetail.CourseDetailActivity;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab.model.CourseStudent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseStudentAdapter extends RecyclerView.Adapter<CourseStudentAdapter.CourseStudentViewHolder> implements ServiceConnection {

    //initiate variables
    private ArrayList<CourseStudent> courseStudentArrayList;
    private SinchService.SinchServiceInterface sinchServiceInterface;

    //constructor class with parameter
    public CourseStudentAdapter(ArrayList<CourseStudent> courseStudentArrayList) {
        this.courseStudentArrayList = courseStudentArrayList;
    }

    //inflate recyclerview cell with layout file
    @NonNull
    @Override
    public CourseStudentAdapter.CourseStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_student_list, parent, false);

        return new CourseStudentViewHolder(view);
    }

    //set data to recyclerview cell
    @Override
    public void onBindViewHolder(@NonNull CourseStudentAdapter.CourseStudentViewHolder holder, int position) {
        //initiate firebase database and firebase storage instance, and get reference path to data/file
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(courseStudentArrayList.get(position).getStudentUID());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users").child(courseStudentArrayList.get(position).getStudentUID());

        //get student name from firebase database
        databaseReference.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if (name!=null) holder.studentName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                holder.toast(holder.context.getString(R.string.retrieve_failed));
            }
        });

        //get student time from firebase database
        databaseReference.child("student").child("courses").child(courseStudentArrayList.get(position).getCourseKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String time = dataSnapshot.getValue(String.class);
                if (time!=null){
                    if (!time.equals("terminate")){
                        holder.studentTime.setText(holder.context.getString(R.string.course_start_time, time));
                    }else{
                        holder.studentTime.setText(holder.context.getString(R.string.terminate_requested_student));
                        holder.studentCallButton.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                holder.toast(holder.context.getString(R.string.retrieve_failed));
            }
        });

        //get student profile image from firebase storage
        storageReference.child("profileimage").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(holder.context).load(uri).into(holder.studentImageView));

        //bind sinch service to context
        holder.context.bindService(new Intent(holder.context, SinchService.class), this, CourseDetailActivity.BIND_AUTO_CREATE);

        //set onclick for call button
        holder.studentCallButton.setOnClickListener((v) -> {
            if (courseStudentArrayList.get(position).getStudentUID()!=null){
                Call call = sinchServiceInterface.callUserVideo(courseStudentArrayList.get(position).getStudentUID());

                String callID = call.getCallId();

                Intent callScreen = new Intent(holder.context, CallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callID);
                holder.context.startActivity(callScreen);
            }
        });
    }

    //return arraylist item as cell size
    @Override
    public int getItemCount() {
        return (courseStudentArrayList != null) ? courseStudentArrayList.size() : 0;
    }

    //connect to sinch service
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (SinchService.class.getName().equals(name.getClassName())){
            sinchServiceInterface = (SinchService.SinchServiceInterface) service;
            onServiceConnected();
        }
    }

    //disconnect from sinch service
    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (SinchService.class.getName().equals(name.getClassName())){
            sinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    //method when sinch service is connected
    public void onServiceConnected(){}

    //method when sinch service is disconnected
    public void onServiceDisconnected(){
        if (sinchServiceInterface!=null){
            sinchServiceInterface.stopClient();
        }
    }

    //recyclerview view holder class
    public static class CourseStudentViewHolder extends RecyclerView.ViewHolder{

        //initiate elements' variable
        private ImageView studentImageView;
        private TextView studentName, studentTime;
        private Button studentCallButton;

        //initiate variable
        private Context context;

        //class constructor
        public CourseStudentViewHolder(@NonNull View itemView) {
            super(itemView);

            studentImageView = itemView.findViewById(R.id.teacherCourse_StudentImage);
            studentName = itemView.findViewById(R.id.teacherCourse_StudentName);
            studentTime = itemView.findViewById(R.id.teacherCourse_StudentTime);
            studentCallButton = itemView.findViewById(R.id.student_call_button);

            itemView.setClickable(true);
            studentImageView.setClipToOutline(true);

            context = itemView.getContext();
        }

        //show toast
        private void toast(String message){
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
