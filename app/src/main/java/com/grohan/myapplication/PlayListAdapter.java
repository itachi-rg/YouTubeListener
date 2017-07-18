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
public class PlayListAdapter extends ArrayAdapter<PlayList> {
    public PlayListAdapter(Context context, List<PlayList> playLists) {
        super(context, 0, playLists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PlayList playList = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            Log.d("PlayListAdap::getView", "inflating");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.each_playlist, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.each_playlist_cover);
        TextView textView = (TextView) convertView.findViewById(R.id.each_playlist_name);
        TextView playlistId = (TextView) convertView.findViewById(R.id.each_playlist_id);

        Bitmap coverImage = playList.getCoverImage();
        if (coverImage != null) {
            Log.d("PlayListAda::getView", "coverImage NOT null");
            imageView.setImageBitmap(coverImage);
        }
        else {
            Log.d("PlayListAda::getView", "coverImage null");
        }

        textView.setText(playList.getName());
        playlistId.setText(playList.getId());


        return convertView;

    }
}
