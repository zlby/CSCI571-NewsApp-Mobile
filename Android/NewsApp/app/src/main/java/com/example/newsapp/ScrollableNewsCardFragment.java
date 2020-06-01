package com.example.newsapp;

import android.annotation.SuppressLint;

import android.content.Context;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONObject;

import java.util.List;

@SuppressLint("ValidFragment")
public class ScrollableNewsCardFragment extends Fragment {
    Context context;
    private RecyclerView recyclerView;
    private RcViewAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private boolean refreshable;
    private ProgressBar progressBar;
    private TextView progressText;
    String url;


    public ScrollableNewsCardFragment(Context context, String url) {
        refreshable = true;
        this.context = context;
        this.url = "http://ec2-54-86-70-47.compute-1.amazonaws.com:9000/guardianapi/" + url;
    }

    public ScrollableNewsCardFragment(Context context, String url, boolean refreshable) {
        this.refreshable = refreshable;
        this.context = context;
        this.url = "http://ec2-54-86-70-47.compute-1.amazonaws.com:9000/guardianapi/" + url;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scroll_news_layout, container, false);
        View v2 = inflater.inflate(R.layout.fg_my_home, container, false);
        if (refreshable) {
            refreshLayout = view.findViewById(R.id.swipe_refresh_news);
            recyclerView = (RecyclerView) view.findViewById(R.id.rc_view_home);
            progressBar = view.findViewById(R.id.progressBar);
            progressText = view.findViewById(R.id.progressText);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    callnewsAPI(refreshLayout, url);
                }
            });
        } else {
            view = inflater.inflate(R.layout.non_refreshable_news_layout, container, false);
            refreshLayout = v2.findViewById(R.id.home_swipe_refresh);
            recyclerView = (RecyclerView) view.findViewById(R.id.rc_view_home_non_refresh);
            progressBar = view.findViewById(R.id.progressBar2);
            progressText = view.findViewById(R.id.progressText2);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new RcViewAdapter(context);
        recyclerView.setAdapter(adapter);

        callnewsAPI(refreshLayout, url);


        return view;
    }

    protected void callnewsAPI(final SwipeRefreshLayout ref, String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        List<NewsObj> items = MyUtils.getInstance(context).dealWithGuardianQuery(response);
                        if (items != null) {
                            adapter.setData(items);
                            adapter.notifyDataSetChanged();
                            if (ref.isRefreshing()) {
                                ref.setRefreshing(false);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            progressText.setVisibility(View.INVISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error Request", error.toString());
                    }
                });

        MyUtils.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void update() {
        adapter.notifyDataSetChanged();
    }
}
