package com.example.learnbit.launch.student.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.userdata.User;
import com.example.learnbit.launch.student.profile.accountsettings.StudentEditProfileActivity;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.example.learnbit.launch.reusableactivity.ChangePasswordActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StudentProfileFragment extends Fragment implements View.OnClickListener {

    private TextView studentName, studentEmail;
    private ImageView studentImage;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ValueEventListener userEventListener;

    private static final String detailPreference = "LOGIN_PREFERENCE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_profile, container, false);

        studentName = view.findViewById(R.id.studentProfile_Name);
        studentEmail = view.findViewById(R.id.studentProfile_Email);
        studentImage = view.findViewById(R.id.studentProfile_ImageView);
        ConstraintLayout editProfileButton = view.findViewById(R.id.studentProfile_EditProfileButton);
        ConstraintLayout changePasswordButton = view.findViewById(R.id.studentProfile_ChangePasswordButton);
        ConstraintLayout shareButton = view.findViewById(R.id.studentProfile_ShareButton);
        ConstraintLayout faqButton = view.findViewById(R.id.studentProfile_FAQButton);
        ConstraintLayout aboutButton = view.findViewById(R.id.studentProfile_AboutButton);
        ConstraintLayout switchButton = view.findViewById(R.id.studentProfile_SwitchButton);
        ConstraintLayout signOutButton = view.findViewById(R.id.studentProfile_SignOutButton);

        studentImage.setClipToOutline(true);

        editProfileButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        faqButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
        switchButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);

        setStatusBarColor();
        setupFirebase();
        retrieveData();
        retrieveDataFromStorage();

        return view;
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        user = firebaseAuth.getCurrentUser();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Users").child(user.getUid()).child("profileimage");
    }

    private void retrieveData(){
        databaseReference = firebaseDatabase.getReference("Users").child(user.getUid());

        userEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user!=null){
                    studentName.setText(user.getName());
                    studentEmail.setText(user.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
            }
        };

        databaseReference.addValueEventListener(userEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(userEventListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.studentProfile_EditProfileButton:
                Intent editProfileIntent = new Intent(getContext(), StudentEditProfileActivity.class);
                startActivity(editProfileIntent);
                break;
            case R.id.studentProfile_ChangePasswordButton:
                Intent changePasswordIntent = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(changePasswordIntent);
                break;
            case R.id.studentProfile_ShareButton:
                shareAction();
                break;
            case R.id.studentProfile_SwitchButton:
                changePreferenceData();
                Intent switchIntent = new Intent(getContext(), TeacherMainActivity.class);
                startActivity(switchIntent);
                break;
            case R.id.studentProfile_SignOutButton:
                firebaseAuth.signOut();
                break;
        }
    }

    private void shareAction(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Join me in learning courses that is taught professional with Learnbit.";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Learnbit for Teacher");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void changePreferenceData(){
        if (getActivity()!=null){
            SharedPreferences preferences = getActivity().getSharedPreferences(detailPreference, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("role", "teacher");
            editor.apply();
        }
    }

    private void retrieveDataFromStorage(){
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            if (getActivity() != null){
                Glide.with(getActivity()).load(uri).into(studentImage);
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show());
    }

    private void setStatusBarColor(){
        if (getActivity()==null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getActivity().getWindow().setStatusBarColor(Color.WHITE);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

}
