package com.example.learnbit.launch.onboard.student;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learnbit.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class StudentOnboardViewpagerAdapter extends PagerAdapter {

    Context mContext;
    List<StudentOnboardElements> mListScreen;

    public StudentOnboardViewpagerAdapter(Context mContext, List<StudentOnboardElements> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = layoutInflater.inflate(R.layout.student_onboard_content, null);

        ImageView onboardImageView = layoutScreen.findViewById(R.id.onboardImageView);
        TextView onboardTitleTV = layoutScreen.findViewById(R.id.onboardTitleTV);
        TextView onboardSubtitleTV = layoutScreen.findViewById(R.id.onboardSubtitleTV);

        onboardImageView.setImageResource(mListScreen.get(position).getImage());
        onboardTitleTV.setText(mListScreen.get(position).getTitle());
        onboardSubtitleTV.setText(mListScreen.get(position).getSubtitle());

        container.addView(layoutScreen);

        return layoutScreen;

    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
