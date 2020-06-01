package com.example.newsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.NETWORK_STATS_SERVICE;

public class MyUtils implements LocationListener {

    private static final long MIN_TIME_BW_UPDATES = 1;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0.5f;
    private static MyUtils singleton;
    private static Context context;

    private RequestQueue requestQueue;

    private LocationManager locationManager;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private double latitude;
    private double longitude;


    private MyUtils(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();

    }


    public static synchronized MyUtils getInstance(Context context) {
        if (singleton == null) {
            singleton = new MyUtils(context);
        }
        return singleton;
    }

    ArrayList<NewsObj> dealWithGuardianQuery(JSONObject jsonObject) {
        try {
            ArrayList<NewsObj> list = new ArrayList<>();
//            JSONObject jsonObject = new JSONObject(resp);
            if (!jsonObject.has("response")
                    || !jsonObject.getJSONObject("response").has("results")
                    || jsonObject.getJSONObject("response").getJSONArray("results").length() == 0) {
                return null;
            }
            JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                list.add(parseGuardian(obj));
            }
            return list;
        } catch (JSONException e) {
            return null;
        }
    }

    NewsObj parseGuardian(JSONObject jsonObject) {

        try {
            String id = jsonObject.getString("id");
            String url = jsonObject.getString("webUrl");
            String title = jsonObject.getString("webTitle");
            // newitem['desc'] = item.blocks.body['0'].bodyTextSummary;
            String desc = getDesc(jsonObject);

            String img = getImgUrl(jsonObject);
            String time = jsonObject.getString("webPublicationDate");
            String tag = jsonObject.getString("sectionName");
            return new NewsObj(id, url, title, desc, img, time, tag);
        } catch (JSONException e) {
            return null;
        }
    }

    private String getImgUrl(JSONObject obj) {
        try {
            if (obj.has("fields") && obj.getJSONObject("fields").has("thumbnail")) {
                return obj.getJSONObject("fields").getString("thumbnail");
            }
            JSONObject blocks = obj.getJSONObject("blocks");
            JSONObject mainobj = blocks.getJSONObject("main");
            JSONArray elements = mainobj.getJSONArray("elements");
            JSONArray assets = elements.getJSONObject(0).getJSONArray("assets");
            return assets.getJSONObject(assets.length()-1).getString("file");
        } catch (JSONException e) {
            return "";
        }
    }

    private String getDesc(JSONObject jsonObject) {
        try {
            JSONArray arr = jsonObject.getJSONObject("blocks").getJSONArray("body");
            String res = "";
            for (int i = 0; i < arr.length(); i++) {
                res += arr.getJSONObject(i).getString("bodyHtml");
            }
//            return jsonObject.getJSONObject("blocks").getJSONArray("body").getJSONObject(0).getString("bodyTextSummary");
            return res;
        } catch (JSONException e) {
            return "";
        }
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        Location location = null;
        try {
            locationManager = (LocationManager) context
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                boolean canGetLocation = true;
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                        assert locationManager != null;
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                }

                if (isNetworkEnabled && location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
//        location = locationManager
//                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if (location != null) {
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
            this.isNetworkEnabled = true;
        }
        else {
            this.isGPSEnabled = true;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
            this.isNetworkEnabled = false;
        }
        else {
            this.isGPSEnabled = false;
        }
    }
}
