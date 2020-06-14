package com.example.learnbit.launch.teacher.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.learnbit.launch.extension.RemoveSinchService;
import com.example.learnbit.launch.extension.SinchService;
import com.example.learnbit.launch.launch.MainActivity;
import com.example.learnbit.launch.model.userdata.teacher.Teacher;
import com.example.learnbit.launch.model.userdata.User;
import com.example.learnbit.launch.student.StudentMainActivity;
import com.example.learnbit.launch.reusableactivity.ChangePasswordActivity;
import com.example.learnbit.launch.teacher.profile.accountsettings.EditProfileActivity;
import com.example.learnbit.launch.teacher.profile.withdraw.WithdrawActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class TeacherProfileFragment extends Fragment implements View.OnClickListener {

    private ImageView teacherProfileImageView;
    private ConstraintLayout withdrawButton, editProfileButton, changePasswordButton, aboutButton, faqButton, shareButton, switchButton, signOutButton;
    private TextView teacherProfileName, teacherProfileEmail, teacherProfileBio, teacherProfileScore, teacherProfileBalance;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private RemoveSinchService removeSinchService;

    private User users = new User();

    private static final String detailPreference = "LOGIN_PREFERENCE";

    public TeacherProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_profile, container, false);

        teacherProfileImageView = view.findViewById(R.id.teacherProfile_ImageView);
        teacherProfileName = view.findViewById(R.id.teacherProfile_Name);
        teacherProfileEmail = view.findViewById(R.id.teacherProfile_Email);
        withdrawButton = view.findViewById(R.id.teacherProfile_WithdrawButton);
        editProfileButton = view.findViewById(R.id.teacherProfile_EditProfileButton);
        changePasswordButton = view.findViewById(R.id.teacherProfile_ChangePasswordButton);
        aboutButton = view.findViewById(R.id.teacherProfile_AboutButton);
        faqButton = view.findViewById(R.id.teacherProfile_FAQButton);
        shareButton = view.findViewById(R.id.teacherProfile_ShareButton);
        switchButton = view.findViewById(R.id.teacherProfile_SwitchButton);
        signOutButton = view.findViewById(R.id.teacherProfile_SignOutButton);
        teacherProfileBio = view.findViewById(R.id.teacherProfile_Bio);
        teacherProfileScore = view.findViewById(R.id.teacherProfile_Score);
        teacherProfileBalance = view.findViewById(R.id.teacherProfile_Balance);

        withdrawButton.setOnClickListener(this);
        editProfileButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
        switchButton.setOnClickListener(this);

        teacherProfileImageView.setClipToOutline(true);

        firebaseSetup();
        retrieveDataFromFirebase();
        retrieveDataFromStorage();

        return view;
    }

    private void firebaseSetup(){
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child(user.getUid());

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Users").child(user.getUid()).child("profileimage");
    }

    private void retrieveDataFromFirebase(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = dataSnapshot.getValue(User.class);
                updateUI(user);

                Teacher teacher = dataSnapshot.child("teacher").getValue(Teacher.class);

                if (teacher!=null){
                    if (teacher.getDescription().equals("")){
                        teacherProfileBio.setText("This teacher has yet to leave a description.");
                    }else{
                        teacherProfileBio.setText(teacher.getDescription());
                    }
                    teacherProfileBalance.setText("IDR " + String.valueOf(teacher.getBalance()));
                    teacherProfileScore.setText(String.format("%.1f", teacher.getRating()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "failed to retrieve profile data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(FirebaseUser user){
        if (user!=null){
            teacherProfileName.setText(users.getName());
            teacherProfileEmail.setText(users.getEmail());
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void retrieveDataFromStorage(){
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            if (getActivity() != null){
                Glide.with(getActivity()).load(uri).into(teacherProfileImageView);
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "failed to retrieve image", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.teacherProfile_WithdrawButton:
                Intent withdrawIntent = new Intent(getContext(), WithdrawActivity.class);
                startActivity(withdrawIntent);
                break;
            case R.id.teacherProfile_EditProfileButton:
                Intent editProfileIntent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(editProfileIntent);
                break;
            case R.id.teacherProfile_ChangePasswordButton:
                Intent changePasswordIntent = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(changePasswordIntent);
                break;
            case R.id.teacherProfile_SignOutButton:
//                removeSinchService.removeSinchService();
                firebaseAuth.signOut();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.teacherProfile_ShareButton:
                shareAction();
                break;
            case R.id.teacherProfile_SwitchButton:
                changePreferenceData("student");
                Intent switchIntent = new Intent(getContext(), StudentMainActivity.class);
                startActivity(switchIntent);
                break;
            default:
                Toast.makeText(getContext(), "Nothing happened", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareAction(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Join me in teaching courses to students from all around the world with Learnbit.";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Learnbit for Teacher");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void changePreferenceData(String role){
        if (getActivity()!=null){
            SharedPreferences preferences = getActivity().getSharedPreferences(detailPreference, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("role", role);
            editor.apply();
        }
    }

}
