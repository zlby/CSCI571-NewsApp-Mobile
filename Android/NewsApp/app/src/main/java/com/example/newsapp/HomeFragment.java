package com.example.newsapp;


import android.annotation.SuppressLint;

import android.content.Context;

import android.location.Address;
import android.location.Geocoder;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;



@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment {
    private Context context;
    private FragmentManager fragmentManager;
    private ScrollableNewsCardFragment fg;
    private String cityName;
    private String stateName;
    private SwipeRefreshLayout layout;
    private View view;

    public HomeFragment(Context context) {
        this.context = context;

    }


    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fg_my_home, container, false);

        updateWeather();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (fg == null) {
            fg = new ScrollableNewsCardFragment(this.getContext(), "newest/top", false);
            layout = view.findViewById(R.id.home_swipe_refresh);
            layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    fg.callnewsAPI(layout,"http://ec2-54-86-70-47.compute-1.amazonaws.com:9000/guardianapi/newest/top");
                    updateWeather();
                }
            });
            fragmentTransaction.add(R.id.home_news_frame, fg);
            fragmentTransaction.show(fg);
        }
        fragmentTransaction.commit();
        return view;
    }

    public void updateWeather() {
        MyUtils.getInstance(this.getContext()).getLocation();

        Geocoder geocoder = new Geocoder(this.getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(MyUtils.getInstance(this.getContext()).getLatitude(), MyUtils.getInstance(this.getContext()).getLongitude(), 1);
        } catch (IOException e) {
            return;
        }
        String cityName = "";
        String stateName = "";
        try {
            cityName = addresses.get(0).getLocality();
            stateName = addresses.get(0).getAdminArea();
        }catch (Exception e) {

        }
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&units=metric&appid=210868b6b32be8c7c193a159df7bd8ab";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String weather = response.getJSONArray("weather").getJSONObject(0).getString("main");
                            String temp = response.getJSONObject("main").getString("temp");
                            long tempint = Math.round(Double.parseDouble(temp));
                            ImageView  imageView = view.findViewById(R.id.weather_img);
                            if (weather.equals("Clouds")) {
                                imageView.setImageResource(R.mipmap.cloudy_weather);
                            } else if (weather.equals("Snow")) {
                                imageView.setImageResource(R.mipmap.snowy_weather);
                            } else if (weather.equals("Rain") || weather.equals("Drizzle")) {
                                imageView.setImageResource(R.mipmap.rainy_weather);
                            } else if (weather.equals("Thunderstorm")) {
                                imageView.setImageResource(R.mipmap.thunder_weather);
                            } else if (weather.equals("Clear")) {
                                imageView.setImageResource(R.mipmap.clear_weather);
                            } else {
                                imageView.setImageResource(R.mipmap.sunny_weather);
                            }
                            TextView tempText = view.findViewById(R.id.text_for_temp);
                            TextView weatherText = view.findViewById(R.id.text_for_weather);
                            String tempstr = tempint + " ℃";
                            tempText.setText(tempstr);
                            weatherText.setText(weather);
                        } catch (JSONException e) {
                            Log.e("Error", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error Request", error.toString());
                    }
                });

        MyUtils.getInstance(context).addToRequestQueue(jsonObjectRequest);



        TextView cityView = view.findViewById(R.id.text_for_city);
        TextView stateView = view.findViewById(R.id.text_for_state);
        cityView.setText(cityName);
        stateView.setText(stateName);
    }

    public void update() {
        if (fg != null) {
            fg.update();
        }
        updateWeather();
    }
}
