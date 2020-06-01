package com.example.newsapp;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    private Toolbar myToolbar;
    private TextView app_title;
    private SearchView search_view;




    private BottomNavigationView bottomNavigationView;

    private HomeFragment home_fg;
    private HeadlineFragment headline_fg;
    private TrendFragment trend_fg;
    private BookmarksFragment bookmarks_fg;

    private FragmentManager fragmentManager;

    private AutoSuggestAdapter newsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        checkRunTimePermission();
        setContentView(R.layout.activity_main);
        bindViews();
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        setSupportActionBar(myToolbar);



//        final androidx.appcompat.widget.SearchView.SearchAutoComplete searchAutoComplete = (androidx.appcompat.widget.SearchView.SearchAutoComplete)search_view.findViewById(R.id.search_src_text);
//        searchAutoComplete.setBackgroundColor(Color.BLUE);
//        searchAutoComplete.setTextColor(Color.GREEN);
//        searchAutoComplete.setDropDownBackgroundResource(android.R.color.holo_blue_light);
//        String dataArr[] = {"Apple" , "Amazon" , "Amd", "Microsoft", "Microwave", "MicroNews", "Intel", "Intelligence"};
//        ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dataArr);
//        searchAutoComplete.setAdapter(newsAdapter);

//        search_view.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                app_title.setVisibility(View.INVISIBLE);
//                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                getSupportActionBar().setDisplayShowHomeEnabled(true);
//            }
//        });
//
//        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
//                Bundle b = new Bundle();
//                b.putString("key", query);
//                intent.putExtras(b);
//                startActivity(intent);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                return false;
//            }
//        });










        fragmentManager = getSupportFragmentManager();
        findViewById(R.id.action_home).performClick();


    }

    private void bindViews() {


        myToolbar = findViewById(R.id.my_main_toolbar);
        app_title = findViewById(R.id.toolbar_title);
//        search_view = findViewById(R.id.my_search_view);

        bottomNavigationView = findViewById(R.id.navbar_bottom);
    }



    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if(home_fg != null)fragmentTransaction.hide(home_fg);
        if(headline_fg != null)fragmentTransaction.hide(headline_fg);
        if(trend_fg != null)fragmentTransaction.hide(trend_fg);
        if(bookmarks_fg != null)fragmentTransaction.hide(bookmarks_fg);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        checkRunTimePermission();
        if (findViewById(R.id.action_home).isSelected()) {
            home_fg.update();
        }
        else if (findViewById(R.id.action_headline).isSelected()) {
            headline_fg.update();
        }
        else if (findViewById(R.id.action_bookmark).isSelected()) {
            bookmarks_fg.update();
        }
//        super.onResume();
    }

    public void checkRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                gpsTracker = new GPSTracker(this);

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        10);
            }
        } else {
//            gpsTracker = new GPSTracker(this); //GPSTracker is class that is used for retrieve user current location
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                gpsTracker = new GPSTracker(this);
                if (home_fg != null) {
                    home_fg.updateWeather();
                }
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // If User Checked 'Don't Show Again' checkbox for runtime permission, then navigate user to Settings
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("Permission Required");
                    dialog.setCancelable(false);
                    dialog.setMessage("You have to Allow permission to access user location");
                    dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",
                                    getPackageName(), null));
                            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(i, 1001);
                        }
                    });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                }
                //code for deny
            }
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        switch (requestCode) {
            case 1001:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                        gpsTracker = new GPSTracker(this);
//                        if (gpsTracker.canGetLocation()) {
//                            latitude = gpsTracker.getLatitude();
//                            longitude = gpsTracker.getLongitude();
//                        }
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},10);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
//            search_btn.setVisibility(View.VISIBLE);
            app_title.setVisibility(View.VISIBLE);
            search_view.setQuery("", false);
            search_view.setIconified(true);
            search_view.clearFocus();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        FragmentTransaction fTransaction = fragmentManager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (item.getItemId()) {
            case R.id.action_home:
                if(home_fg == null){
                    home_fg = new HomeFragment(this);
                    fTransaction.add(R.id.ly_content,home_fg);
                }else{
                    home_fg.updateWeather();
                    home_fg.update();
                    fTransaction.show(home_fg);
                }
                break;
            case R.id.action_headline:
                if(headline_fg == null){
                    headline_fg = new HeadlineFragment();
                    fTransaction.add(R.id.ly_content,headline_fg);
                }else{
                    headline_fg.update();
                    fTransaction.show(headline_fg);
                }
                break;
            case R.id.action_trend:
                if(trend_fg == null){
                    trend_fg = new TrendFragment();
                    fTransaction.add(R.id.ly_content,trend_fg);
                }else{
                    fTransaction.show(trend_fg);
                }
                break;
            case R.id.action_bookmark:
                if(bookmarks_fg == null){
                    bookmarks_fg = new BookmarksFragment(this);
                    fTransaction.add(R.id.ly_content,bookmarks_fg);
                }else{
                    bookmarks_fg.update();
                    fTransaction.show(bookmarks_fg);
                }
                break;
            default:
                return false;
        }
        fTransaction.commit();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the search menu action bar.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);

        // Get the search menu.
        MenuItem searchMenu = menu.findItem(R.id.action_search);

        // Get SearchView object.
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchMenu.getActionView();



        final androidx.appcompat.widget.SearchView.SearchAutoComplete searchAutoComplete = (androidx.appcompat.widget.SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);


        searchAutoComplete.setBackgroundColor(Color.WHITE);
        searchAutoComplete.setTextColor(Color.BLACK);
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.white);

        // Create a new ArrayAdapter and add data to search auto complete object.
//        String dataArr[] = {"Apple" , "Amazon" , "Amd", "Microsoft", "Microwave", "MicroNews", "Intel", "Intelligence"};

//        newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        newsAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
        searchAutoComplete.setAdapter(newsAdapter);

        // Listen to search view item on click event.
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
            }
        });

        // Below event is triggered when submit search query.
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                Bundle b = new Bundle();
                b.putString("key", query);
                intent.putExtras(b);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() < 3) {
                    newsAdapter.setData(new ArrayList<String>());
                    newsAdapter.notifyDataSetChanged();
                    return false;
                }
                getSuggestList(newText);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    void getSuggestList(String keyword) {
        final ArrayList<String> res = new ArrayList<>();
        String url = "https://api.cognitive.microsoft.com/bing/v7.0/suggestions?q=" + keyword;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("suggestionGroups").getJSONObject(0).getJSONArray("searchSuggestions");
                            for (int i = 0; i  < Math.min(5, arr.length()); i++) {
                                res.add(arr.getJSONObject(i).getString("displayText"));
                            }
                            newsAdapter.setData(res);
                            newsAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            newsAdapter.clear();
                            newsAdapter.notifyDataSetChanged();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error Request", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Ocp-Apim-Subscription-Key", "811d4ddadbc04c5f97c11a35604c8ed5");
                return params;
            }
        };

        MyUtils.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
