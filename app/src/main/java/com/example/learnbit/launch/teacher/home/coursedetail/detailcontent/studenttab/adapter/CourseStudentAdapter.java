package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab.adapter;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.extension.BaseActivity;
import com.example.learnbit.launch.extension.SinchService;
import com.example.learnbit.launch.reusableactivity.CallScreenActivity;
import com.example.learnbit.launch.teacher.home.coursedetail.CourseDetailActivity;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab.model.CourseStudent;
import com.google.android.gms.tasks.OnSuccessListener;
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
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class CourseStudentAdapter extends RecyclerView.Adapter<CourseStudentAdapter.CourseStudentViewHolder> implements ServiceConnection {

    private ArrayList<CourseStudent> courseStudentArrayList;
    private SinchService.SinchServiceInterface sinchServiceInterface;

    public CourseStudentAdapter(ArrayList<CourseStudent> courseStudentArrayList) {
        this.courseStudentArrayList = courseStudentArrayList;
    }

    @NonNull
    @Override
    public CourseStudentAdapter.CourseStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_student_list, parent, false);

        return new CourseStudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseStudentAdapter.CourseStudentViewHolder holder, int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(courseStudentArrayList.get(position).getStudentUID());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users").child(courseStudentArrayList.get(position).getStudentUID());

        databaseReference.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);

                if (name!=null) holder.studentName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(holder.context, "failed to retrieve student name", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.child("student").child("courses").child(courseStudentArrayList.get(position).getCourseKey()).child("courseTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String time = dataSnapshot.getValue(String.class);

                if (time!=null) holder.studentTime.setText("Course will start at " + time);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(holder.context, "failed to retrieve course time", Toast.LENGTH_SHORT).show();
            }
        });

        storageReference.child("profileimage").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(holder.context).load(uri).into(holder.studentImageView));

        holder.context.bindService(new Intent(holder.context, SinchService.class), this, CourseDetailActivity.BIND_AUTO_CREATE);

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

    @Override
    public int getItemCount() {
        return (courseStudentArrayList != null) ? courseStudentArrayList.size() : 0;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (SinchService.class.getName().equals(name.getClassName())){
            sinchServiceInterface = (SinchService.SinchServiceInterface) service;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (SinchService.class.getName().equals(name.getClassName())){
            sinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    public void onServiceConnected(){
        Log.d("message", "onservice connected.");
    }

    public void onServiceDisconnected(){
    }

    public static class CourseStudentViewHolder extends RecyclerView.ViewHolder{

        private ImageView studentImageView;
        private TextView studentName, studentTime;
        private final Context context;
        private Button studentCallButton;

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

        public void changeCallButton(Boolean permissionGranted){
            if (permissionGranted){
                studentCallButton.setClickable(true);
            }else{
                studentCallButton.setClickable(false);
            }
        }
    }
}
