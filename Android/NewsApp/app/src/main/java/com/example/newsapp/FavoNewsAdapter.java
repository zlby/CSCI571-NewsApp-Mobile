package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

public class FavoNewsAdapter extends RecyclerView.Adapter<FavoNewsAdapter.MyFavoViewHolder> {
    private Context context;
    private List<NewsObj> data;
    private LayoutInflater layoutInflater;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private BookmarksFragment f;

    FavoNewsAdapter(Context context, BookmarksFragment f) {
        this.context = context;
        this.f = f;
        this.layoutInflater = LayoutInflater.from(this.context);
        this.data = new ArrayList<>();
        pref = context.getSharedPreferences("MyBookmarks", 0);
        editor = pref.edit();

    }

    @NonNull
    @Override
    public FavoNewsAdapter.MyFavoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.my_favo_news_card, parent, false);
        return new MyFavoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoNewsAdapter.MyFavoViewHolder holder, final int position) {
        final NewsObj newsObj = data.get(position);
        holder.titleView.setText(newsObj.getTitle());
        if (!newsObj.getImg().equals("")) {
            Picasso.with(this.context).load(newsObj.getImg()).resize(400, 400).into(holder.imageView);
        }
        holder.imageView.setVisibility(View.VISIBLE);
        String subtitle = newsObj.getDateWithoutYear() + " | " + newsObj.getTag();
        holder.subtitleView.setText(subtitle);
        holder.bookmark.setSelected(true);
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
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.costum_dialog);
                ImageView imageView = dialog.findViewById(R.id.img_in_dialog);
                TextView text = (TextView) dialog.findViewById(R.id.dialog_title_text);
                final ImageView bookmark = dialog.findViewById(R.id.dialog_bookmark);
                ImageView sharebtn = dialog.findViewById(R.id.dialog_twitter);
                bookmark.setSelected(true);
                bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = "\"" + newsObj.getTitle() + "\"";
                        message += " was removed from bookmarks";
                        editor.remove(newsObj.getId());
                        data.remove(position);
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        editor.commit();
                        f.update();
                        dialog.dismiss();
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
                holder.bookmark.setSelected(false);
                message += " was removed from bookmarks";
                editor.remove(newsObj.getId());
                data.remove(position);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                editor.commit();

                f.update();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<NewsObj> data) {
        this.data = data;
    }

    public static class MyFavoViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        ImageView imageView;
        TextView subtitleView;
        ImageView bookmark;

        public MyFavoViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.title_in_favo);
            imageView = itemView.findViewById(R.id.image_in_favo);
            subtitleView = itemView.findViewById(R.id.date_in_favo);
            bookmark = itemView.findViewById(R.id.bookmark_in_favo);
        }
    }
}
