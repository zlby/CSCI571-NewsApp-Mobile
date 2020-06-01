package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RcViewAdapter extends RecyclerView.Adapter<RcViewAdapter.MyViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<NewsObj> data;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    RcViewAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(this.context);
        this.data = new ArrayList<>();
        pref = context.getSharedPreferences("MyBookmarks", 0);
        editor = pref.edit();
    }

    @NonNull
    @Override
    public RcViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.my_news_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final NewsObj newsObj = data.get(position);
        holder.titleView.setText(newsObj.getTitle());
        if (!newsObj.getImg().equals("")) {
            Picasso.with(this.context).load(newsObj.getImg()).resize(400, 400).into(holder.imageView);
        }
        String subtitle = newsObj.getTimeFromNow() + " ago | " + newsObj.getTag();
        holder.subtitleView.setText(subtitle);
        if (pref.contains(newsObj.getId())) {
            holder.bookmark.setSelected(true);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ArticleActivity.class);
                Bundle b = new Bundle();
                b.putString("id", newsObj.getId());
                intent.putExtras(b);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.costum_dialog);
                ImageView imageView = dialog.findViewById(R.id.img_in_dialog);
                TextView text = (TextView) dialog.findViewById(R.id.dialog_title_text);
                final ImageView bookmark = dialog.findViewById(R.id.dialog_bookmark);
                ImageView sharebtn = dialog.findViewById(R.id.dialog_twitter);
                if (pref.contains(newsObj.getId())) {
                    bookmark.setSelected(true);
                }
                else {
                    bookmark.setSelected(false);
                }
                bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = "\"" + newsObj.getTitle() + "\"";
                        if (holder.bookmark.isSelected()) {
                            holder.bookmark.setSelected(false);
                            bookmark.setSelected(false);
                            message += " was removed from bookmarks";
                            editor.remove(newsObj.getId());
                        }
                        else {
                            holder.bookmark.setSelected(true);
                            bookmark.setSelected(true);
                            message += " was added to bookmarks";
                            editor.putString(newsObj.getId(), newsObj.toString());
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        editor.commit();
                    }
                });
                sharebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tweetUrl = "https://twitter.com/intent/tweet?text=Check out this Link: &url="
                                + newsObj.getUrl() + "&hashtags=CSCI571NewsSearch";
                        Uri uri = Uri.parse(tweetUrl);
                        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                });
                Picasso.with(context).load(newsObj.getImg()).resize(500, 500).into(imageView);
                text.setText(newsObj.getTitle());
                dialog.show();
                return true;
            }
        });

        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "\"" + newsObj.getTitle() + "\"";
                if (holder.bookmark.isSelected()) {
                    holder.bookmark.setSelected(false);
                    message += " was removed from bookmarks";
                    editor.remove(newsObj.getId());
                }
                else {
                    holder.bookmark.setSelected(true);
                    message += " was added to bookmarks";
                    editor.putString(newsObj.getId(), newsObj.toString());
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                editor.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        ImageView imageView;
        TextView subtitleView;
        ImageView bookmark;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.news_card_title_view);
            imageView = itemView.findViewById(R.id.news_card_img);
            subtitleView = itemView.findViewById(R.id.date_tag_text);
            bookmark = itemView.findViewById(R.id.bookmark_in_hr_card);
        }
    }

    public void setData(List<NewsObj> data) {
        this.data = data;
    }
}
