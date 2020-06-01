package com.example.newsapp;



import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class HeadlineFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fg_my_headline,container,false);
        viewPager = view.findViewById(R.id.ly_scroll_news_content);
        adapter = new ViewPageAdapter(getContext(), getFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    public void update() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

}
