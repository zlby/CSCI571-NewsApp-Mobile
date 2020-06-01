package com.example.newsapp;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class ViewPageAdapter extends FragmentPagerAdapter {
    private String section[] = {"world", "business", "politics", "sports", "technology", "science"};
    private Context context;

    public ViewPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new ScrollableNewsCardFragment(context, section[position]);
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return section[position].toUpperCase();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
