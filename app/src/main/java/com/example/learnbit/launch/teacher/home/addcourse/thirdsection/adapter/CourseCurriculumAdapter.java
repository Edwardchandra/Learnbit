package com.example.learnbit.launch.teacher.home.addcourse.thirdsection.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model.Curriculum;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseCurriculumAdapter extends RecyclerView.Adapter<CourseCurriculumAdapter.CourseCurriculumViewHolder>{

    private ArrayList<Curriculum> curriculumArrayList = new ArrayList<>();
    private Context context;

    public CourseCurriculumAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CourseCurriculumAdapter.CourseCurriculumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_curriculum_edittext, parent, false);

        return new CourseCurriculumAdapter.CourseCurriculumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CourseCurriculumAdapter.CourseCurriculumViewHolder holder, final int position) {
        holder.sectionWeek.setText(context.getString(R.string.week_count, (position+1)));

        holder.sectionNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                curriculumArrayList.get(position).setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.sectionTopicOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                curriculumArrayList.get(position).setTopicA(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.sectionTopicTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                curriculumArrayList.get(position).setTopicB(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.sectionTopicThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                curriculumArrayList.get(position).setTopicC(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.sectionTopicOneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                curriculumArrayList.get(position).setSpinnerA(parent.getItemAtPosition(pos).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        holder.sectionTopicTwoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                curriculumArrayList.get(position).setSpinnerB(parent.getItemAtPosition(pos).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        holder.sectionTopicThreeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                curriculumArrayList.get(position).setSpinnerC(parent.getItemAtPosition(pos).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public int getItemCount() {
        return (curriculumArrayList == null) ? 0 : curriculumArrayList.size();
    }

    class CourseCurriculumViewHolder extends RecyclerView.ViewHolder {

        //elements variables
        private EditText sectionNameET;
        private TextView sectionWeek;
        private EditText sectionTopicOne;
        private EditText sectionTopicTwo;
        private EditText sectionTopicThree;
        private Spinner sectionTopicOneSpinner;
        private Spinner sectionTopicTwoSpinner;
        private Spinner sectionTopicThreeSpinner;

        //constructors
        CourseCurriculumViewHolder(@NonNull final View itemView) {
            super(itemView);

            sectionNameET = itemView.findViewById(R.id.addCourse_CourseSectionName);
            sectionWeek = itemView.findViewById(R.id.addCourse_CourseCurriculumWeek);

            sectionTopicOne = itemView.findViewById(R.id.addCourse_CourseTopicOne);
            sectionTopicTwo = itemView.findViewById(R.id.addCourse_CourseTopicTwo);
            sectionTopicThree = itemView.findViewById(R.id.addCourse_CourseTopicThree);

            sectionTopicOneSpinner = itemView.findViewById(R.id.addCourse_CourseTopicOneSpinner);
            sectionTopicTwoSpinner = itemView.findViewById(R.id.addCourse_CourseTopicTwoSpinner);
            sectionTopicThreeSpinner = itemView.findViewById(R.id.addCourse_CourseTopicThreeSpinner);

            setupSpinner(sectionTopicOneSpinner);
            setupSpinner(sectionTopicTwoSpinner);
            setupSpinner(sectionTopicThreeSpinner);
        }

        //setup spinner
        private void setupSpinner(Spinner spinner){
            ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(context, R.array.course_type_array, android.R.layout.simple_spinner_item);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);
        }
    }

    //add new curriculum edittext cell to recyclerview
    public void addEditText(Curriculum curriculum){
        curriculumArrayList.add(curriculumArrayList.size(), curriculum);
        notifyItemInserted(curriculumArrayList.size() - 1);
    }

    //retrieve arraylist data
    public ArrayList<Curriculum> getArrayList() {
        return curriculumArrayList;
    }

}
