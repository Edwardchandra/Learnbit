package com.example.learnbit.launch.teacher.home.addcourse.firstsection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbit.BuildConfig;
import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.addcourse.secondsection.AddSecondSectionActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddFirstSectionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner courseCategorySpinner, courseSubcategorySpinner;
    private EditText courseNameET, courseSummaryET;
    private Toolbar firstSectionToolbar;
    private Button courseNextButton;

    private String cameraFilePath;

    private Intent galleryIntent;
    private Intent cameraIntent;

    private String spinnerValue = "";
    private String subSpinnerValue = "";

    private ArrayAdapter<CharSequence> categoryAdapter;
    private ArrayAdapter<CharSequence> languageAdapter;
    private ArrayAdapter<CharSequence> personalDevAdapter;
    private ArrayAdapter<CharSequence> computerAdapter;
    private ArrayAdapter<CharSequence> mathAdapter;
    private ArrayAdapter<CharSequence> naturalAdapter;
    private ArrayAdapter<CharSequence> socialAdapter;
    private ArrayAdapter<CharSequence> artAdapter;
    private ArrayAdapter<CharSequence> civicAdapter;

    private File courseImageFilePath;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_first_section);

        courseCategorySpinner = (Spinner) findViewById(R.id.addCourse_CourseCategory);
        courseSubcategorySpinner = (Spinner) findViewById(R.id.addCourse_CourseSubcategory);
        courseNameET = (EditText) findViewById(R.id.addCourse_CourseNameET);
        courseSummaryET = (EditText) findViewById(R.id.addCourse_CourseSummaryET);
        courseNextButton = (Button) findViewById(R.id.addCourse_NextButton);
        firstSectionToolbar = (Toolbar) findViewById(R.id.firstSectionToolbar);

        courseNextButton.setOnClickListener(this);

        setupSpinner(categoryAdapter, R.array.category_array, courseCategorySpinner);
        setupToolbar();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupToolbar(){
        setSupportActionBar(firstSectionToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void setupSpinner(ArrayAdapter<CharSequence> arrayAdapter, int textArray, Spinner spinner){
        arrayAdapter = ArrayAdapter.createFromResource(this, textArray, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void subSpinnerSetup(){
        switch (spinnerValue){
            case "Language and Literature":
                setupSpinner(languageAdapter, R.array.language_array, courseSubcategorySpinner);
                Toast.makeText(this, spinnerValue, Toast.LENGTH_SHORT).show();
                break;
            case "Personal Development":
                setupSpinner(personalDevAdapter, R.array.personal_development_array, courseSubcategorySpinner);
                break;
            case "Computer Technology":
                setupSpinner(computerAdapter, R.array.computer_array, courseSubcategorySpinner);
                break;
            case "Mathematics and Logic":
                setupSpinner(mathAdapter, R.array.mathematics_array, courseSubcategorySpinner);
                break;
            case "Natural Science":
                setupSpinner(naturalAdapter, R.array.natural_array, courseSubcategorySpinner);
                break;
            case "Social Science":
                setupSpinner(socialAdapter, R.array.social_array, courseSubcategorySpinner);
                break;
            case "Art and Culture":
                setupSpinner(artAdapter, R.array.art_array, courseSubcategorySpinner);
                break;
            case "Civic Education":
                setupSpinner(civicAdapter, R.array.civic_array, courseSubcategorySpinner);
                break;
            default:
                Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;

        switch (spinner.getId()){
            case R.id.addCourse_CourseCategory:
                spinnerValue = parent.getItemAtPosition(position).toString();
                TextView selectedValue = (TextView) parent.getChildAt(0);
                if (selectedValue!=null){
                    selectedValue.setTextColor(getResources().getColor(android.R.color.black));
                }
                subSpinnerSetup();
            case R.id.addCourse_CourseSubcategory:
                subSpinnerValue = parent.getItemAtPosition(position).toString();
                TextView subselectedvalue = (TextView) parent.getChildAt(0);
                if (subselectedvalue!=null){
                    subselectedvalue.setTextColor(getResources().getColor(android.R.color.black));
                }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

//    private File createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
//        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
//        cameraFilePath = "file://" + image.getAbsolutePath();
//        return image;
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK)
//            switch (requestCode){
//                case 0:
//                    Uri selectedImage = data.getData();
//                    courseImageView.setImageURI(selectedImage);
//                    break;
//                case 1:
//                    courseImageView.setImageURI(Uri.parse(cameraFilePath));
//                    break;
//            }
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

//    private void pickFromGallery() {
//        galleryIntent = new Intent(Intent.ACTION_PICK);
//        galleryIntent.setType("image/*");
//        String[] mimeTypes = {"image/jpeg", "image/png"};
//        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        startActivityForResult(galleryIntent, 0);
//    }
//
//    private void captureFromCamera() {
//        try {
//            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
//            startActivityForResult(cameraIntent, 1);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }

    private void checkEditText(){
        if (courseNameET.getText().toString().isEmpty()){
            courseNameET.setError("Course name shouldn't be empty");
        }else if (courseSummaryET.getText().toString().isEmpty()){
            courseSummaryET.setError("Course summary shouldn't be empty");
        }else{
//            BitmapDrawable bitmapDrawable = (BitmapDrawable) courseImageView.getDrawable();
//            Bitmap bitmap = bitmapDrawable.getBitmap();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] bitmapData = baos.toByteArray();
            Intent intent = new Intent(getApplicationContext(), AddSecondSectionActivity.class);
            intent.putExtra("courseName", courseNameET.getText().toString());
            intent.putExtra("courseCategory", spinnerValue);
            intent.putExtra("courseSubcategory", subSpinnerValue);
            intent.putExtra("courseSummary", courseSummaryET.getText().toString());
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addCourse_NextButton:
                checkEditText();
                break;
            default:
                Toast.makeText(this, "nothing happened", Toast.LENGTH_SHORT).show();
        }
    }
}
