package com.example.newsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookmarksFragment extends Fragment {

    private Context context;
    private RecyclerView recyclerView;
    private FavoNewsAdapter adapter;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private TextView no_book_mark;

    BookmarksFragment(Context context) {
        this.context = context;
        pref = context.getSharedPreferences("MyBookmarks", 0);
        editor = pref.edit();
        adapter = new FavoNewsAdapter(context, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_my_bookmarks,container,false);

        recyclerView = view.findViewById(R.id.rcview_bookmark);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
        no_book_mark = view.findViewById(R.id.no_bookmark_text);
        update();

        return view;
    }

    public void extractPref() {
        List<NewsObj> items = new ArrayList<>();
        Map<String, ?> keys = pref.getAll();
        if (keys.size() == 0) {
            no_book_mark.setVisibility(View.VISIBLE);
        }
        else {
            no_book_mark.setVisibility(View.INVISIBLE);
        }
        for(Map.Entry<String,?> entry : keys.entrySet()){
            String newsinfo = entry.getValue().toString();
            items.add(new NewsObj(newsinfo));
        }
        adapter.setData(items);
    }



    public void update() {
        extractPref();
        adapter.notifyDataSetChanged();
    }
}
