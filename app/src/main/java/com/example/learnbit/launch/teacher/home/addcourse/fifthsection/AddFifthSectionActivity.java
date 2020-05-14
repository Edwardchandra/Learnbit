package com.example.learnbit.launch.teacher.home.addcourse.fifthsection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.learnbit.BuildConfig;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.teacher.home.addcourse.fifthsection.model.Date;
import com.example.learnbit.launch.teacher.home.addcourse.fifthsection.model.Section;
import com.example.learnbit.launch.teacher.home.addcourse.fifthsection.model.SectionTopic;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.example.learnbit.launch.teacher.home.addcourse.secondsection.model.Time;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model.Benefit;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model.Curriculum;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model.Requirement;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class AddFifthSectionActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout addCourseImageButton;
    private Button createCourseButton;
    private ImageView courseImageView;

    private String courseName, courseCategory, courseSubcategory, courseSummary, courseStartDate, courseEndDate, courseImageURL;
    private String[] courseScheduleArray;
    private ArrayList<Time> courseTimeArrayList;
    private ArrayList<Benefit> courseBenefitArrayList;
    private ArrayList<Requirement> courseRequirementArrayList;
    private ArrayList<Curriculum> courseCurriculumArrayList;
    private long coursePrice = 0;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String cameraFilePath;
    private Intent galleryIntent, cameraIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fifth_section);

        addCourseImageButton = findViewById(R.id.addCourse_ChangeImageButton);
        createCourseButton = findViewById(R.id.addCourse_CreateButton);
        courseImageView = findViewById(R.id.addCourse_ImageView);

        addCourseImageButton.setOnClickListener(this);
        createCourseButton.setOnClickListener(this);

        getIntentData();
        setupFirebase();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addCourse_ChangeImageButton:
                pickFromGallery();
                break;
            case R.id.addCourse_CreateButton:
                uploadCourseImage();
                Toast.makeText(this, "Your course has been submitted and will be reviewed by us. Please expect 2-3 days of approval", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, TeacherMainActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "nothing happened", Toast.LENGTH_SHORT).show();
        }
    }

    private void getIntentData(){
        courseName = getIntent().getStringExtra("courseName");
        courseCategory = getIntent().getStringExtra("courseCategory");
        courseSubcategory = getIntent().getStringExtra("courseSubcategory");
        courseSummary = getIntent().getStringExtra("courseSummary");
        courseStartDate = getIntent().getStringExtra("courseStartDate");
        courseEndDate = getIntent().getStringExtra("courseEndDate");

        courseScheduleArray = getIntent().getStringArrayExtra("courseScheduleArray");
        courseTimeArrayList = getIntent().getParcelableArrayListExtra("courseTimeArrayList");
        courseBenefitArrayList = getIntent().getParcelableArrayListExtra("courseBenefitArrayList");
        courseRequirementArrayList = getIntent().getParcelableArrayListExtra("courseRequirementArrayList");
        courseCurriculumArrayList = getIntent().getParcelableArrayListExtra("courseCurriculumArrayList");

        String passedPrice = getIntent().getStringExtra("coursePrice");
        if (passedPrice != null){
            coursePrice = Long.parseLong(passedPrice);
        }
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        user = firebaseAuth.getCurrentUser();
    }

    private void pickFromGallery() {
        galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(galleryIntent, 0);
    }

    private void captureFromCamera() {
        try {
            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
            startActivityForResult(cameraIntent, 1);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        cameraFilePath = "file://" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case 0:
                    Uri selectedImage = data.getData();
                    courseImageView.setImageURI(selectedImage);
                    break;
                case 1:
                    courseImageView.setImageURI(Uri.parse(cameraFilePath));
                    break;
            }
    }

    private void createCourse(){
        HashMap<String, String> courseSchedule = new HashMap<>();
        HashMap<String, String> courseBenefit = new HashMap<>();
        HashMap<String, Boolean> courseTime = new HashMap<>();
        HashMap<String, String> courseRequirement = new HashMap<>();

        for (int i=0;i<courseScheduleArray.length;i++) {
            courseSchedule.put("day " + (i+1), courseScheduleArray[i]);
        }

        for (int i=0;i<courseBenefitArrayList.size();i++){
            courseBenefit.put("benefit " + (i+1), courseBenefitArrayList.get(i).getBenefit());
        }

        for (int i=0;i<courseRequirementArrayList.size();i++){
            courseRequirement.put("requirement " + (i+1), courseRequirementArrayList.get(i).getRequirement());
        }

        for (int i=0;i<courseTimeArrayList.size();i++){
            courseTime.put(courseTimeArrayList.get(i).getTime(), false);
        }

        databaseReference = firebaseDatabase.getReference("Course").child(user.getUid()).push();

        databaseReference.setValue(new Course(courseName, courseSummary, coursePrice, false, 0, courseCategory, courseSubcategory, courseImageURL));
        databaseReference.child("courseDate").setValue(new Date(courseStartDate, courseEndDate));
        databaseReference.child("courseSchedule").setValue(courseSchedule);

        for (int i=0;i<courseTimeArrayList.size();i++){
            databaseReference.child("courseTime").setValue(courseTime);
        }

        for (int i=0;i<courseBenefitArrayList.size();i++){
            databaseReference.child("courseBenefit").setValue(courseBenefit);
        }

        for (int i=0;i<courseRequirementArrayList.size();i++){
            databaseReference.child("courseRequirement").setValue(courseRequirement);
        }

        for (int i=0;i<courseCurriculumArrayList.size();i++){
            databaseReference.child("courseCurriculum").child("week " + (i + 1)).setValue(new Section(courseCurriculumArrayList.get(i).getName()));

            for (int j=0;j<courseCurriculumArrayList.get(i).getTopics().length;j++){
                databaseReference.child("courseCurriculum").child("week " + (i + 1)).child("topics").child("part " + (j + 1))
                        .setValue(new SectionTopic(courseCurriculumArrayList.get(i).getTopics()[j], courseCurriculumArrayList.get(i).getSpinners()[j]));
            }
        }
    }

    private void uploadCourseImage(){
        storageReference = firebaseStorage.getReference("Course").child(user.getUid()).child(courseName).child("courseImage");

        Drawable imageDrawable = courseImageView.getDrawable();

        if (imageDrawable != null){
            Bitmap bitmap = ((BitmapDrawable) imageDrawable).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageReference.putBytes(data);
            uploadTask
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to upload profile image", Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getApplicationContext(), "Successfully upload profile image", Toast.LENGTH_SHORT).show();
                        if (taskSnapshot.getMetadata()!=null){
                            if (taskSnapshot.getMetadata().getReference()!=null){
                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(uri -> {
                                    courseImageURL = uri.toString();
                                    Log.d("courseImageURL", courseImageURL);
                                    createCourse();
                                });
                            }
                        }
                    });
        }
    }
}
