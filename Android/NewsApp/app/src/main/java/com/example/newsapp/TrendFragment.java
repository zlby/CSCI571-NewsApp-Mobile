package com.example.newsapp;


import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import com.github.mikephil.charting.data.Entry;


public class TrendFragment extends Fragment {
    private List<Entry> dataset;
    private LineChart lineChart;
    private TextInputEditText textEditor;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_my_trend,container,false);
        lineChart = view.findViewById(R.id.trend_chart);
        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(18);
        legend.setTextColor(Color.BLACK);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        generateDataSet("Coronavirus");
        textEditor = view.findViewById(R.id.input_trend_keyword);
        textEditor.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    generateDataSet(textEditor.getText().toString());
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    public void generateDataSet(final String keyword) {
        String url = "http://ec2-54-86-70-47.compute-1.amazonaws.com:9000/trendapi/" + keyword;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dataset = parseTrendResp(response);
                        if (dataset != null) {
                            LineDataSet lineDataSet = new LineDataSet(dataset, "Trending Chart for " + keyword);
                            lineDataSet.setColor(getResources().getColor(R.color.colorPrimary));
                            lineDataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
                            lineDataSet.setValueTextSize(8);
                            lineDataSet.setValueTextColor(getResources().getColor(R.color.colorPrimary));
                            lineChart.setData(new LineData(lineDataSet));
                            lineChart.invalidate();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error Request", error.toString());
                    }
                });

        MyUtils.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private List<Entry> parseTrendResp(JSONObject resp) {
        try {
            List<Entry> res = new ArrayList<>();
            JSONArray arr = resp.getJSONObject("default").getJSONArray("timelineData");
            for (int i = 0; i < arr.length(); i++) {
                int value = Integer.parseInt(arr.getJSONObject(i).getJSONArray("value").getString(0));
                Entry e = new Entry(i, value);
                res.add(e);
            }
            return res;
        }catch (JSONException e) {
            return null;
        }
    }

}
