package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.submitscore;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.submitscore.adapter.SelectStudentAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SelectStudentFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private String courseKey;
    private ArrayList<String> studentArrayList = new ArrayList<>();
    private SelectStudentAdapter selectStudentAdapter;

    //initiate preference key to retrieve shared preference data
    private static final String detailPreference = "DETAIL_PREFERENCE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_student, container, false);


        recyclerView = view.findViewById(R.id.studentRecyclerView);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> {
            dismiss();
        });

        getPreferenceData();
        setupRecyclerView();
        retrieveData();

        return view;
    }

    public void onResume(){
        super.onResume();
        if (getDialog()!=null){
            Window window = getDialog().getWindow();
            if (window!=null){
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                window.setGravity(Gravity.CENTER);
            }
        }
    }

    private void setupRecyclerView(){
        selectStudentAdapter = new SelectStudentAdapter(studentArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(selectStudentAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void retrieveData(){
        FirebaseDatabase.getInstance().getReference("Course").child(courseKey).child("courseStudent").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String userUid = ds.getValue(String.class);
                    if (userUid!=null){
                        studentArrayList.add(userUid);
                    }
                }
                selectStudentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (getActivity()!=null){
                    toast(getActivity().getString(R.string.retrieve_failed));
                }
            }
        });
    }

    private void toast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    //retrieve stored data from shared preference
    private void getPreferenceData(){
        String preferenceKey = "courseKey";

        if (getActivity()!=null){
            SharedPreferences preferences = getActivity().getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
            courseKey = preferences.getString(preferenceKey, "");
        }
    }
}