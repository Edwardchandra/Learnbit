package com.example.learnbit.launch.student.course.material;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.adapter.FilesAdapter;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.adapter.MaterialAdapter;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.adapter.SubmitAdapter;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.model.File;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.model.Material;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.model.Submit;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MaterialActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView materialTV, submitTV, uploadTV;
    private RecyclerView materialRecyclerView, submitRecyclerView, uploadRecyclerView;

    private static final int RESULT_UPLOAD_FILES = 0;

    //initiate file and material adapter
    private FilesAdapter filesAdapter;
    private MaterialAdapter materialAdapter;
    private SubmitAdapter submitAdapter;

    //initiate variables
    private ArrayList<File> fileArray = new ArrayList<>();
    private ArrayList<Material> materialArrayList = new ArrayList<>();
    private ArrayList<Submit> submitArrayList = new ArrayList<>();
    private ArrayList<String> materialKeyArrayList = new ArrayList<>();
    private ArrayList<String> submitKeyArrayList = new ArrayList<>();
    private String courseName, courseSectionTopic, courseWeek, courseSectionPart, courseKey, courseSectionType;

    //initiate firebase variables
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser user;

    //initiate preference key to retrieve shared preference data
    private static final String detailPreference = "DETAIL_PREFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        materialTV = findViewById(R.id.materialTV);
        submitTV = findViewById(R.id.submitTV);
        uploadTV = findViewById(R.id.uploadTV);
        materialRecyclerView = findViewById(R.id.materialRV);
        submitRecyclerView = findViewById(R.id.submitRV);
        uploadRecyclerView = findViewById(R.id.uploadRV);
        FloatingActionButton addButton = findViewById(R.id.addFilesButton);

        addButton.setOnClickListener(this);

        retrieveIntentData();
        getPreferenceData();
        setupToolbar();
        setupSubmitRecyclerView();
        setupFilesRecyclerView();
        setupMaterialRecyclerView();
        validateContent();
        setupFirebase();
        retrieveData();
        retrieveSubmitData();
        handleRecyclerViewSwipe();
    }

    //retrieve stored data from shared preference
    private void getPreferenceData(){
        String preferenceKey = "courseKey";

        SharedPreferences preferences = getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
        courseKey = preferences.getString(preferenceKey, "");
    }

    //setup custom toolbar
    private void setupToolbar(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Materials");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    //setup file recyclerview
    private void setupFilesRecyclerView(){
        filesAdapter = new FilesAdapter(fileArray);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        uploadRecyclerView.setLayoutManager(layoutManager);
        uploadRecyclerView.setAdapter(filesAdapter);
    }

    //setup material recyclerview
    private void setupMaterialRecyclerView(){
        materialAdapter = new MaterialAdapter(materialArrayList, materialKeyArrayList, courseWeek, courseName, courseSectionPart, courseSectionTopic);
        RecyclerView.LayoutManager materialLayoutManager = new LinearLayoutManager(this);
        materialRecyclerView.setLayoutManager(materialLayoutManager);
        materialRecyclerView.setAdapter(materialAdapter);
    }

    //retrieve passed intent data
    private void retrieveIntentData(){
        courseName = getIntent().getStringExtra("courseName");
        courseSectionTopic = getIntent().getStringExtra("courseSectionTopic");
        courseWeek = getIntent().getStringExtra("courseWeek");
        courseSectionPart = getIntent().getStringExtra("courseSectionPart");
        courseSectionType = getIntent().getStringExtra("courseSectionType");
    }

    private void validateContent(){
        if (courseSectionType.equals("Video Call")){
            submitTV.setVisibility(View.INVISIBLE);
            submitRecyclerView.setVisibility(View.INVISIBLE);
        }else if (courseSectionType.equals("Quiz")){
            setupSubmitRecyclerView();
        }
    }

    private void setupSubmitRecyclerView(){
        submitAdapter = new SubmitAdapter(submitArrayList, submitKeyArrayList, courseWeek, courseName, courseSectionPart);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        submitRecyclerView.setLayoutManager(layoutManager);
        submitRecyclerView.setAdapter(submitAdapter);
    }

    private void retrieveSubmitData(){
        submitArrayList.clear();
        submitKeyArrayList.clear();

        Query query = databaseReference
                .child(courseKey)
                .child("courseCurriculum")
                .child(courseWeek)
                .child("topics")
                .child(courseSectionPart)
                .child("submittedFile");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                Submit submit = dataSnapshot.getValue(Submit.class);

                if (submit!=null){
                    submitArrayList.add(new Submit(submit.getSubmitFileName(), submit.getSubmitFileUrl(), submit.getUserUid()));
                    submitKeyArrayList.add(key);
                    submitAdapter.notifyItemInserted(submitArrayList.size() - 1);

                    if (submitArrayList.size() != 0){
                        submitTV.setVisibility(View.VISIBLE);
                        submitRecyclerView.setVisibility(View.VISIBLE);
                    }else{
                        submitTV.setVisibility(View.GONE);
                        submitRecyclerView.setVisibility(View.GONE);
                    }
                }

                submitAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                submitAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                submitAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                submitAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    //setup firebase instance
    private void setupFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        user = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference("Course");
        storageReference = firebaseStorage.getReference("Course").child(courseName).child(courseWeek).child("topics").child(courseSectionPart).child("submittedFile");
    }

    //retrieve material data
    private void retrieveData(){
        materialArrayList.clear();
        materialKeyArrayList.clear();

        Query query = databaseReference
                .child(courseKey)
                .child("courseCurriculum")
                .child(courseWeek)
                .child("topics")
                .child(courseSectionPart)
                .child("sectionTopicMaterials");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                Material material = dataSnapshot.getValue(Material.class);

                if (material!=null){
                    materialArrayList.add(new Material(material.getMaterialName(), material.getMaterialURL()));
                    materialKeyArrayList.add(key);
                    materialAdapter.notifyItemInserted(materialArrayList.size() - 1);

                    if (materialArrayList.size() != 0){
                        materialRecyclerView.setVisibility(View.VISIBLE);
                        materialTV.setVisibility(View.VISIBLE);
                    }else{
                        materialRecyclerView.setVisibility(View.GONE);
                        materialTV.setVisibility(View.GONE);
                    }

                    materialAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                materialAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                materialAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                materialAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                materialAdapter.notifyDataSetChanged();
            }
        });
    }

    //select files from file manager
    private void selectFiles(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Files"), RESULT_UPLOAD_FILES);
    }

    //select files activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_UPLOAD_FILES && resultCode == Activity.RESULT_OK){
            uploadRecyclerView.setVisibility(View.VISIBLE);
            if (data!=null){
                fileArray.clear();
                filesAdapter.notifyDataSetChanged();

                if (data.getClipData() != null){
                    uploadTV.setVisibility(View.VISIBLE);

                    int totalSelectedItem = data.getClipData().getItemCount();

                    for (int i=0;i<totalSelectedItem;i++){
                        Uri fileUri = data.getClipData().getItemAt(i).getUri();
                        String fileName = getFileName(fileUri);

                        fileArray.add(new File(fileName, false));
                        filesAdapter.notifyDataSetChanged();

                        final int position = i;

                        storageReference.child(fileName).putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
                            if (taskSnapshot.getMetadata()!=null){
                                if (taskSnapshot.getMetadata().getReference()!=null){
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(uri -> {
                                        String topicURL = uri.toString();
                                        String topicName = fileArray.get(position).getName();

                                        databaseReference
                                                .child(courseKey)
                                                .child("courseCurriculum")
                                                .child(courseWeek)
                                                .child("topics")
                                                .child(courseSectionPart)
                                                .child("submittedFile")
                                                .push()
                                                .setValue(new Submit(topicName, topicURL, user.getUid())).addOnSuccessListener(aVoid -> submitAdapter.notifyDataSetChanged());
                                    });
                                }
                            }
                        });
                    }

                }else if (data.getData() != null){
                    uploadTV.setVisibility(View.VISIBLE);
                    Uri fileUri = data.getData();
                    String fileName = getFileName(fileUri);

                    fileArray.add(new File(fileName, false));
                    filesAdapter.notifyDataSetChanged();

                    storageReference.child(fileName).putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
                        if (taskSnapshot.getMetadata()!=null){
                            if (taskSnapshot.getMetadata().getReference()!=null){
                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(uri -> {
                                    String topicURL = uri.toString();
                                    String topicName = fileArray.get(fileArray.size()-1).getName();

                                    databaseReference
                                            .child(courseKey)
                                            .child("courseCurriculum")
                                            .child(courseWeek)
                                            .child("topics")
                                            .child(courseSectionPart)
                                            .child("submittedFile")
                                            .push()
                                            .setValue(new Submit(topicName, topicURL, user.getUid())).addOnSuccessListener(aVoid -> submitAdapter.notifyDataSetChanged());
                                });
                            }
                        }
                    });
                }
            }
        }
    }

    //get selected file name
    private String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme()!=null && uri.getScheme().equals("content")){
            if (getApplicationContext() != null){
                try (Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            }
        }

        if (result == null){
            result = uri.getPath();
            if (result != null){
                int cut = result.lastIndexOf('/');
                if (cut != -1){
                    result = result.substring(cut + 1);
                }
            }
        }

        return result;
    }

    //execute elements action when clicked
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addFilesButton){
            selectFiles();
        }
    }

    //recyclerview swipe actions
    private void handleRecyclerViewSwipe(){
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                switch (direction){
                    case ItemTouchHelper.LEFT:
                    case ItemTouchHelper.RIGHT:
                        setDeleteAlertAt(position);
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                ColorDrawable background = new ColorDrawable(Color.RED);
                Drawable icon = getDrawable(R.drawable.ic_delete);

                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 25;

                if (icon!=null){
                    int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconBottom = iconTop + icon.getIntrinsicHeight();

                    if (dX > 0) { // Swiping to the right
                        int iconLeft = itemView.getLeft() + iconMargin;
                        int iconRight = iconLeft + icon.getIntrinsicWidth();
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                        background.setBounds(itemView.getLeft(), itemView.getTop(),
                                itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
                    } else if (dX < 0) { // Swiping to the left
                        int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                        int iconRight = itemView.getRight() - iconMargin;
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                        background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                                itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    } else { // view is unSwiped
                        icon.setBounds(0, 0, 0, 0);
                        background.setBounds(0, 0, 0, 0);
                    }

                    background.draw(c);
                    icon.draw(c);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(submitRecyclerView);
    }

    //show delete notice
    private void setDeleteAlertAt(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(getString(R.string.delete_file_notice));
        builder.setPositiveButton("YES", (dialog, which) -> deleteItem(position));

        builder.setNegativeButton("NO", (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteItem(int position){
        databaseReference
                .child(courseKey)
                .child("courseCurriculum")
                .child(courseWeek)
                .child("topics")
                .child(courseSectionPart)
                .child("submittedFile").child(submitKeyArrayList.get(position)).removeValue();

        submitKeyArrayList.remove(position);
        submitArrayList.remove(position);
        submitAdapter.notifyItemRemoved(position);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}