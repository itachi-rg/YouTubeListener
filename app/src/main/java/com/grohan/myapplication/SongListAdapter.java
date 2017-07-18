package com.grohan.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rg on 01-Jun-15.
 */
public class SongListAdapter extends ArrayAdapter<Song> {
    public SongListAdapter(Context context, List<Song> songs) {
        super(context, 0, songs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Song song = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            Log.d("SongListAdap::getView", "Inflating");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.each_song, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.each_song_cover);
        TextView textView = (TextView) convertView.findViewById(R.id.each_song_name);

        Bitmap coverImage = song.getCoverImage();
        if (coverImage != null) {
            imageView.setImageBitmap(coverImage);
        }
        textView.setText(song.getName());

        return convertView;

    }
}
