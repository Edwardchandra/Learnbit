package com.example.learnbit.launch.teacher.home.addcourse.secondsection.adapter;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.addcourse.secondsection.AddSecondSectionActivity;
import com.example.learnbit.launch.teacher.home.addcourse.secondsection.model.Time;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

public class CourseTimeAdapter extends RecyclerView.Adapter<CourseTimeAdapter.CourseTimeViewHolder> {

    private ArrayList<Time> arrayList = new ArrayList<>();
    private Context context;

    public CourseTimeAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CourseTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_time_picker_edittext, parent, false);

        return new CourseTimeAdapter.CourseTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseTimeViewHolder holder, final int position) {
        holder.courseTimeET.setHint(context.getString(R.string.time_hint));

        holder.courseTimeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                arrayList.get(position).setTime(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList == null) ? 0 : arrayList.size();
    }

    public class CourseTimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private EditText courseTimeET;
        private Calendar calendar = Calendar.getInstance();

        public CourseTimeViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTimeET = itemView.findViewById(R.id.addCourse_CourseTime);
            courseTimeET.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            TimePickerFragment timePickerFragment = new TimePickerFragment();

            timePickerFragment.timeSetListener = (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateTime();
            };

            timePickerFragment.show(((AddSecondSectionActivity)context).getSupportFragmentManager(), "timePicker");
        }

        private void updateTime(){
            String myFormat = "hh:mm aa";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            courseTimeET.setText(sdf.format(calendar.getTime()));
        }
    }

    public static class TimePickerFragment extends DialogFragment{
        TimePickerDialog.OnTimeSetListener timeSetListener;

        public TimePickerFragment() {}

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            return new TimePickerDialog(getContext(), timeSetListener, hour, minute, true);
        }
    }

    public void addEditText(Time time){
        arrayList.add(arrayList.size(), time);
        notifyItemInserted(arrayList.size() - 1);
    }

    public ArrayList<Time> getArrayList(){
        return arrayList;
    }
}


