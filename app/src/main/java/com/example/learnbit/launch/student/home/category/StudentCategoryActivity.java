package com.example.learnbit.launch.student.home.category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.student.home.category.adapter.CategoryAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class StudentCategoryActivity extends AppCompatActivity {

    private RecyclerView subCategoryRecyclerView;
    private Toolbar subCategoryToolbar;
    private TextView categoryNameTV, categoryDefinitionTV;
    private ArrayList<String> subCategoryArrayList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_category);

        subCategoryRecyclerView = findViewById(R.id.subCategoryRecyclerView);
        subCategoryToolbar = findViewById(R.id.subCategoryToolbar);
        categoryNameTV = findViewById(R.id.categoryName);
        categoryDefinitionTV = findViewById(R.id.categoryDefinition);

        setupToolbar();
        retrieveIntentData();
        setupRecyclerView();
    }

    private void setupToolbar(){
        setSupportActionBar(subCategoryToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private void retrieveIntentData(){
        String categoryName = getIntent().getStringExtra("categoryName");

        if (categoryName!=null){
            String filterCategoryName = categoryName.replace("\n", "");
            categoryNameTV.setText(filterCategoryName);

            switch (filterCategoryName){
                case "Language & Literature":
                    categoryDefinitionTV.setText("A courses develop linguistic and literary understanding and skills through the study of a broad range of genres and world literature, as well as language learning in context.");
                    convertAndSaveData(R.array.language_array);
                    break;
                case "Personal Development":
                    categoryDefinitionTV.setText("Courses and Learning that is focused to improve strategies and frameworks for personal growth, goal setting, and self improvement.");
                    convertAndSaveData(R.array.personal_development_array);
                    break;
                case "Computer Technology":
                    categoryDefinitionTV.setText("Computer technology courses cover everything from computer hardware assembly and system design to data storage and network security to electronics and computer engineering.");
                    convertAndSaveData(R.array.computer_array);
                    break;
                case "Mathematics & Logic":
                    categoryDefinitionTV.setText("The study of the measurement, properties, and relationships of quantities and sets, using numbers and symbols.");
                    convertAndSaveData(R.array.mathematics_array);
                    break;
                case "Natural Science":
                    categoryDefinitionTV.setText("A branch of science which deals with the physical world, e.g. mathematical sciences, physics, chemistry, and biology.");
                    convertAndSaveData(R.array.natural_array);
                    break;
                case "Social Science":
                    categoryDefinitionTV.setText("Social science is an academic discipline concerned with society and the relationships among individuals within a society, which often rely primarily on empirical approaches.");
                    convertAndSaveData(R.array.social_array);
                    break;
                case "Art & Culture":
                    categoryDefinitionTV.setText("An interdisciplinary, visual theory-based course established around the belief that visual literacy and the impact of visual forms of thinking and working play significant roles in our society.");
                    convertAndSaveData(R.array.art_array);
                    break;
                case "Civic Education":
                    categoryDefinitionTV.setText("Civic education is the study of the theoretical, political and practical aspects of citizenship, as well as its rights and duties.");
                    convertAndSaveData(R.array.civic_array);
                    break;
            }
        }
    }

    private void setupRecyclerView(){
        categoryAdapter = new CategoryAdapter(subCategoryArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        subCategoryRecyclerView.setLayoutManager(layoutManager);
        subCategoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void convertAndSaveData(int id){
        String[] array = getResources().getStringArray(id);
        Collections.addAll(subCategoryArrayList, array);
    }
}
