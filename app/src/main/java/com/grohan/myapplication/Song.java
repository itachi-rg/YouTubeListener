package com.grohan.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by rg on 01-Jun-15.
 */
public class Song {
    String id;
    String name;
    Bitmap cover;
    Activity songActivity;
    static int totalSongCount;
    static int count;

    Song(Activity songActivity, String id, String name, String imageUrl, int songCount) {
        this.songActivity = songActivity;
        this.id = id;
        this.name = name;
        Song.totalSongCount = songCount;
        new GetImageByUrl(Song.this, imageUrl).execute();
    }

    public void updatePlaylistView() {
    //    ((SongListActivity)songActivity).updateSonglistView();
        Log.d("Song::updatePlaylist", "song count " + Integer.toString(Song.count));
        Log.d("Song::updatePlaylist", "totalSongCount" + Integer.toString(Song.totalSongCount));
        if(Song.count == Song.totalSongCount) {
            Song.count=0;
            ((SongListActivity)songActivity).stopSpinner();
        }
        ((SongListActivity)songActivity).updateSonglistView();
    }

    public String getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }

    public Bitmap getCoverImage() {
        //Log.d("Song::getCoverImage", "getting cover image");
        return this.cover;
    }

    public void setBitmap(Bitmap bm) {
        this.cover = bm;
        Song.count++;
        //Log.d("Song::setBitmap", "setting cover image " + Integer.toString(Song.count));
    }
}
