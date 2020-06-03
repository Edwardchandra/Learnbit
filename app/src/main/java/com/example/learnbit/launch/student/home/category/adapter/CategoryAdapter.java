package com.example.learnbit.launch.student.home.category.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.SubCategoryViewHolder> {

    private ArrayList<String> subCategoryArrayList;
    private ArrayList<Course> courseArrayList = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    public CategoryAdapter(ArrayList<String> subCategoryArrayList) {
        this.subCategoryArrayList = subCategoryArrayList;
    }

    @NonNull
    @Override
    public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_subcategory, parent, false);

        return new SubCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {
        holder.name.setText(subCategoryArrayList.get(position));

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query query = firebaseDatabase.getReference("Course");

        setupFirebase();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String key = ds.getKey();

                    if (key!=null){

                        firebaseDatabase.getReference("Course").child(key).orderByChild("courseSubcategory").equalTo(subCategoryArrayList.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds1 : dataSnapshot.getChildren()){
                                    Course course = ds1.getValue(Course.class);

                                    if (course!=null){

                                        if (course.getCourseStudent()!=null){
                                            HashMap<String, String> courseStudent =  course.getCourseStudent();

                                            for (HashMap.Entry<String, String> entry : courseStudent.entrySet()){
                                                String value = entry.getValue();

                                                if (value.equals(user.getUid())){
                                                    courseArrayList.add(new Course(course.getCourseName(), 0, course.getCourseImageURL(), course.getCourseRating()));
                                                }else{
                                                    courseArrayList.add(new Course(course.getCourseName(), course.getCoursePrice(), course.getCourseImageURL(), course.getCourseRating()));
                                                }
                                            }

                                        }else{
                                            courseArrayList.add(new Course(course.getCourseName(), course.getCoursePrice(), course.getCourseImageURL(), course.getCourseRating()));
                                        }

                                        SubcategoryAdapter subcategoryAdapter = new SubcategoryAdapter(courseArrayList, key);
                                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                                        holder.recyclerView.setLayoutManager(layoutManager);
                                        holder.recyclerView.setAdapter(subcategoryAdapter);
                                        holder.recyclerView.setHasFixedSize(true);
                                    }
                                }
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

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    @Override
    public int getItemCount() {
        return (subCategoryArrayList == null) ? 0 : subCategoryArrayList.size();
    }

    public class SubCategoryViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private RecyclerView recyclerView;

        public SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.subCategoryName);
            recyclerView = itemView.findViewById(R.id.contentRecyclerView);
        }
    }
}
