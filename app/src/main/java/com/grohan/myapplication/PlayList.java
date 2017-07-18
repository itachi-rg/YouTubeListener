package com.grohan.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by rg on 31-May-15.
 */
public class PlayList {
    Activity mainActivity;
    String id;
    String name;
    Bitmap cover;

    static int count=0;
    int totalPlaylistCount;

    PlayList(Activity mainActivity, String id, String name, String imageUrl, int playlistCount) {
        this.mainActivity = mainActivity;
        this.id = id;
        this.name = name;
        this.totalPlaylistCount = playlistCount;
        new GetImageByUrl(PlayList.this, imageUrl).execute();
    }

    public void updatePlaylistView() {
        Log.d("PL::updatePlayListView", "update Playlist number : " + Integer.toString(this.count));
        Log.d("PL::updatePlayListView", "Total number of playlists : " + Integer.toString(this.totalPlaylistCount));
        if(PlayList.count == this.totalPlaylistCount) {
            PlayList.count=0;
            ((MainActivity) mainActivity).stopSpinner();
            new GetSongUrl(this.mainActivity).execute();
        }
        ((MainActivity)mainActivity).updatePlaylistView();
    }

    public String getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public Bitmap getCoverImage() {
        Log.d("PlayList::getCoverImage", "getting cover image");
        return this.cover;
    }
    public void setBitmap(Bitmap bm) {
        Log.d("PlayList::setBitmap", "setting cover image");
        this.cover = bm;
        PlayList.count++;
    }


}
