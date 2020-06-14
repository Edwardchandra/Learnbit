package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.model.File;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.model.Material;
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

public class SupportingFilesActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView uploadingTextView;
    private RecyclerView filesRecyclerView, materialRecyclerView;

    private static final int RESULT_UPLOAD_FILES = 0;

    private ArrayList<File> fileArray = new ArrayList<>();
    private FilesAdapter filesAdapter;

    private ArrayList<Material> materialArrayList = new ArrayList<>();
    private MaterialAdapter materialAdapter;

    private Uri fileUri;
    private String fileName;

    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private String courseName, courseSectionTopic, courseWeek, courseSectionPart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supporting_files);

        materialRecyclerView = findViewById(R.id.uploadedRecyclerView);
        filesRecyclerView = findViewById(R.id.filesRecyclerView);
        uploadingTextView = findViewById(R.id.uploadingTextView);
        FloatingActionButton addFilesButton = findViewById(R.id.addFilesButton);

        setupToolbar();
        retrieveIntentData();
        setupFilesRecyclerView();
        setupFirebase();
        retrieveData();
        setupMaterialRecyclerView();
        handleRecyclerViewSwipe();

        addFilesButton.setOnClickListener(this);
    }

    private void setupToolbar(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Add Materials");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        user = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference("Course");
        storageReference = firebaseStorage.getReference("Course").child(user.getUid()).child(courseName).child(courseWeek).child("topics").child(courseSectionPart).child(courseSectionTopic);
    }

    private void setupFilesRecyclerView(){
        filesAdapter = new FilesAdapter(fileArray, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        filesRecyclerView.setLayoutManager(layoutManager);
        filesRecyclerView.setAdapter(filesAdapter);
    }

    private void setupMaterialRecyclerView(){
        materialAdapter = new MaterialAdapter(materialArrayList, courseWeek, courseName, courseSectionPart, courseSectionTopic);
        RecyclerView.LayoutManager materialLayoutManager = new LinearLayoutManager(getApplicationContext());
        materialRecyclerView.setLayoutManager(materialLayoutManager);
        materialRecyclerView.setAdapter(materialAdapter);
    }

    private void retrieveIntentData(){
        courseName = getIntent().getStringExtra("courseName");
        courseSectionTopic = getIntent().getStringExtra("courseSectionTopic");
        courseWeek = getIntent().getStringExtra("courseWeek");
        courseSectionPart = getIntent().getStringExtra("courseSectionPart");
    }

    private void retrieveData(){
        Query query = databaseReference.child(user.getUid()).orderByChild("courseName").startAt(courseName);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                materialArrayList.clear();
                materialAdapter.notifyDataSetChanged();

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String key = ds.getKey();

                    if (key!=null){
                        Query query1 = databaseReference.child(user.getUid())
                                .child(key)
                                .child("courseCurriculum")
                                .child(courseWeek)
                                .child("topics")
                                .child(courseSectionPart)
                                .child("sectionTopicMaterials");

                        query1.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                Material material = dataSnapshot.getValue(Material.class);

                                if (material!=null){
                                    materialArrayList.add(new Material(material.getMaterialName(), material.getMaterialURL()));
                                    materialAdapter.notifyItemInserted(materialArrayList.size() - 1);


                                    if (materialArrayList.size() != 0){
                                        materialRecyclerView.setVisibility(View.VISIBLE);
                                    }
                                }

                                materialAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void selectFiles(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Files"), RESULT_UPLOAD_FILES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_UPLOAD_FILES && resultCode == Activity.RESULT_OK){
            if (data!=null){
                if (data.getClipData() != null){
                    uploadingTextView.setVisibility(View.VISIBLE);

                    int totalSelectedItem = data.getClipData().getItemCount();

                    for (int i=0;i<totalSelectedItem;i++){
                        fileUri = data.getClipData().getItemAt(i).getUri();
                        fileName = getFileName(fileUri);

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
                                        Log.d("courseURL", topicURL);

                                        Query query = databaseReference.child(user.getUid()).orderByChild("courseName").startAt(courseName);

                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                for (DataSnapshot ds : dataSnapshot.getChildren()){
                                                    String key = ds.getKey();

                                                    if (key!=null){
                                                        databaseReference.child(user.getUid())
                                                                .child(key)
                                                                .child("courseCurriculum")
                                                                .child(courseWeek)
                                                                .child("topics")
                                                                .child(courseSectionPart)
                                                                .child("sectionTopicMaterials")
                                                                .child("material " + (materialArrayList.size() + 1))
                                                                .setValue(new Material(topicName, topicURL)).addOnSuccessListener(aVoid -> {
                                                                    fileArray.get(position).setUpload(true);

                                                                    filesAdapter.notifyDataSetChanged();
                                                                });
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    });
                                }
                            }
                        });

                    }

                }else if (data.getData() != null){
                    Toast.makeText(getApplicationContext(), "select single files", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addFilesButton){
            selectFiles();
        }else{
            Toast.makeText(this, "Nothing happened", Toast.LENGTH_SHORT).show();
        }
    }

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
                        setDeleteAlertAt(position);
                        break;
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
        itemTouchHelper.attachToRecyclerView(materialRecyclerView);
    }

    private void setDeleteAlertAt(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to delete this file?");
        builder.setPositiveButton("YES", (dialog, which) -> {
            deleteItem(position);

            materialAdapter.notifyDataSetChanged();
        });

        builder.setNegativeButton("NO", (dialog, which) -> {
            materialAdapter.notifyDataSetChanged();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteItem(int position){
        Query query = databaseReference.child(user.getUid()).orderByChild("courseName").startAt(courseName);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();

                    if (key!=null){
                        databaseReference.child(user.getUid())
                                .child(key)
                                .child("courseCurriculum")
                                .child(courseWeek)
                                .child("topics")
                                .child(courseSectionPart)
                                .child("sectionTopicMaterials").child("material " + (position + 1)).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }


}
