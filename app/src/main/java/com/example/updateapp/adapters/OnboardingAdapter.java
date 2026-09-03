package com.example.updateapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.updateapp.R;

public class OnboardingAdapter extends PagerAdapter {

    private final Context context;

    private final int[] titles = {
            R.string.onboarding_title_1,
            R.string.onboarding_title_2,
            R.string.onboarding_title_3
    };

    private final int[] subtitles = {
            R.string.onboarding_subtitle_1,
            R.string.onboarding_subtitle_2,
            R.string.onboarding_subtitle_3
    };

    private final int[] images = {
            R.drawable.bg_1,
            R.drawable.bg_2,
            R.drawable.bg_3
    };

    public OnboardingAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.slide, container, false);

        ImageView image = v.findViewById(R.id.slideImg);
        TextView title = v.findViewById(R.id.sliderTitle);
        TextView subtitle = v.findViewById(R.id.sliderSubTitle);

        image.setImageResource(images[position]);
        title.setText(context.getString(titles[position]));
        subtitle.setText(context.getString(subtitles[position]));

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
