package com.example.learnbit.launch.teacher.profile;

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
    private TextView teacherProfileName, teacherProfileEmail, teacherProfileBio, teacherProfileScore, teacherProfileBalance;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

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
        ConstraintLayout withdrawButton = view.findViewById(R.id.teacherProfile_WithdrawButton);
        ConstraintLayout editProfileButton = view.findViewById(R.id.teacherProfile_EditProfileButton);
        ConstraintLayout changePasswordButton = view.findViewById(R.id.teacherProfile_ChangePasswordButton);
        ConstraintLayout aboutButton = view.findViewById(R.id.teacherProfile_AboutButton);
        ConstraintLayout faqButton = view.findViewById(R.id.teacherProfile_FAQButton);
        ConstraintLayout shareButton = view.findViewById(R.id.teacherProfile_ShareButton);
        ConstraintLayout switchButton = view.findViewById(R.id.teacherProfile_SwitchButton);
        ConstraintLayout signOutButton = view.findViewById(R.id.teacherProfile_SignOutButton);
        teacherProfileBio = view.findViewById(R.id.teacherProfile_Bio);
        teacherProfileScore = view.findViewById(R.id.teacherProfile_Score);
        teacherProfileBalance = view.findViewById(R.id.teacherProfile_Balance);

        teacherProfileImageView.setClipToOutline(true);

        withdrawButton.setOnClickListener(this);
        editProfileButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
        switchButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
        faqButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);

        firebaseSetup();
        retrieveDataFromFirebase();
        retrieveDataFromStorage();

        return view;
    }

    private void firebaseSetup(){
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child(user.getUid());

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Users").child(user.getUid()).child("profileimage");
    }

    private void retrieveDataFromFirebase(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = dataSnapshot.getValue(User.class);
                updateUI(user);

                Teacher teacher = dataSnapshot.child("teacher").getValue(Teacher.class);

                if (teacher!=null){
                    if (getActivity()!=null){
                        if (teacher.getDescription().equals("")){
                            teacherProfileBio.setText(getActivity().getString(R.string.description_empty));
                        }else{
                            teacherProfileBio.setText(teacher.getDescription());
                        }
                        teacherProfileBalance.setText(getActivity().getString(R.string.price, teacher.getBalance()));
                        teacherProfileScore.setText(getActivity().getString(R.string.rating_text, teacher.getRating()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (getActivity()!=null){
                    toast(getActivity().getString(R.string.retrieve_failed));
                }
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
        }).addOnFailureListener(e -> toast(getString(R.string.retrieve_failed)));
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
                firebaseAuth.signOut();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.teacherProfile_ShareButton:
                shareAction();
                break;
            case R.id.teacherProfile_SwitchButton:
                changePreferenceData();
                Intent switchIntent = new Intent(getContext(), StudentMainActivity.class);
                startActivity(switchIntent);
                break;
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

    private void changePreferenceData(){
        if (getActivity()!=null){
            SharedPreferences preferences = getActivity().getSharedPreferences(detailPreference, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("role", "student");
            editor.apply();
        }
    }

    private void toast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
