package com.example.newsapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;



public class ArticleActivity extends AppCompatActivity {
    private String articleId;
    private ImageView imageView;
    private TextView titleView;
    private TextView tagView;
    private TextView timeView;
    private TextView descView;
    private TextView linkView;
    private TextView headView;
    private CardView progressCard;
    private ImageView bookmark;
    private ImageView sharebtn;
    private Toolbar toolbar;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_article);
        bindViews();
        Bundle b = getIntent().getExtras();
        articleId = b.getString("id");
        callAPI(articleId);

    }

    void bindViews() {
        pref = getSharedPreferences("MyBookmarks", 0);
        editor = pref.edit();
        imageView = findViewById(R.id.img_in_article);
        titleView = findViewById(R.id.title_text_in_article);
        tagView = findViewById(R.id.tag_text_in_article);
        timeView = findViewById(R.id.time_text_in_article);
        descView = findViewById(R.id.desc_text_in_article);
        linkView = findViewById(R.id.view_full_article);
        headView = findViewById(R.id.tool_bar_title);
        progressCard = findViewById(R.id.progress_card);
//        progressCard.bringToFront();
        bookmark = findViewById(R.id.article_bookmark);
        sharebtn = findViewById(R.id.article_twitter);
        toolbar = findViewById(R.id.article_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
    }

    void callAPI(final String articleId) {
        String url = "http://ec2-54-86-70-47.compute-1.amazonaws.com:9000/guardianapi/detailed/" + articleId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        NewsObj newsObj = null;
                        try {
                            JSONObject obj = response.getJSONObject("response").getJSONObject("content");
                            newsObj = MyUtils.getInstance(getApplicationContext()).parseGuardian(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        titleView.setText(newsObj.getTitle());
                        headView.setText(newsObj.getTitle());
                        tagView.setText(newsObj.getTag());
                        timeView.setText(newsObj.getDate());
                        descView.setVisibility(View.INVISIBLE);
                        descView.setText(Html.fromHtml(newsObj.getDesc(), Html.FROM_HTML_MODE_LEGACY));
                        if (pref.contains(articleId)) {
                            bookmark.setSelected(true);
                        }
                        final NewsObj finalNewsObj = newsObj;
                        bookmark.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String message = "\"" + finalNewsObj.getTitle() + "\"";
                                if (bookmark.isSelected()) {
                                    bookmark.setSelected(false);
                                    message += " was removed from bookmarks";
                                    editor.remove(finalNewsObj.getId());
                                }
                                else {
                                    bookmark.setSelected(true);
                                    message += " was added to bookmarks";
                                    editor.putString(finalNewsObj.getId(), finalNewsObj.toString());
                                }
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                editor.commit();
                            }
                        });
                        final NewsObj finalNewsObj1 = newsObj;
                        sharebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String tweetUrl = "https://twitter.com/intent/tweet?text=Check out this Link: &url="
                                        + finalNewsObj1.getUrl() + "&hashtags=CSCI571NewsSearch";
                                Uri uri = Uri.parse(tweetUrl);
                                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                            }
                        });
                        linkView.setText(Html.fromHtml("<a style=\"color:#ffffff\" href=\""+ newsObj.getUrl() + "\">" + getResources().getString(R.string.article_link_str) + "</a>"));
                        linkView.setClickable(true);
                        linkView.setLinkTextColor(getResources().getColor(R.color.lightdark));
                        linkView.setMovementMethod (LinkMovementMethod.getInstance());
                        if (!newsObj.getImg().equals("")) {
                            Picasso.with(getApplicationContext()).load(newsObj.getImg()).resize(1000, 600).into(imageView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    descView.setVisibility(View.VISIBLE);
                                    imageView.setVisibility(View.VISIBLE);
                                    progressCard.setVisibility(View.INVISIBLE);
                                }
                                @Override
                                public void onError() {
                                    descView.setVisibility(View.INVISIBLE);
                                    imageView.setVisibility(View.VISIBLE);
                                    progressCard.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                        else {
                            descView.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.VISIBLE);
                            progressCard.setVisibility(View.INVISIBLE);
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error Request", error.toString());
                    }
                });

        MyUtils.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

//    @Override
//    public void onClick(View v) {
//        finish();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
