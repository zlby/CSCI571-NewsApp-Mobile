package com.example.newsapp;



import android.os.Bundle;
import android.view.MenuItem;

import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class SearchActivity extends AppCompatActivity {
    private ScrollableNewsCardFragment fg;
    private TextView titleView;
    private Toolbar toolbar;
    private String keyword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_layout);
        bindviews();
        Bundle b = getIntent().getExtras();
        keyword = b.getString("key");
        String title = "Search Results for " + keyword;
        titleView.setText(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fg == null) {
            fg = new ScrollableNewsCardFragment(this, "search/" + keyword);
            fragmentTransaction.add(R.id.search_result_frame, fg);
            fragmentTransaction.show(fg);
        }
        fragmentTransaction.commit();
    }

    protected void bindviews() {
        titleView = findViewById(R.id.search_result_title);
        toolbar = findViewById(R.id.search_result_toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fg != null) {
            fg.update();
        }
    }
}
