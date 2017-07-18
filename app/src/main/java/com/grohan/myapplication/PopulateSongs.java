package com.grohan.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rg on 04-Jun-15.
 */
public class PopulateSongs extends AsyncTask<Void, Void, List<Song>>{
    String songList;
    SongListActivity songListActivity;
    List<Song> songs;

    PopulateSongs(SongListActivity songListActivity, String songList) {
        this.songListActivity = songListActivity;
        this.songList = songList;
    }


    @Override
    protected List<Song> doInBackground(Void... params) {
        try {
            Log.d("PopulateSongs::dIB", "================================ Populating Songs ================================");
            JSONObject jsonObject = new JSONObject(songList);


            SongListActivity.totalSongs = (jsonObject.getJSONObject("pageInfo")).getInt("totalResults");
            if (SongListActivity.totalSongs > 50) {
                try {
                    SongListActivity.nextPageToken = jsonObject.getString("nextPageToken");
                }
                catch (JSONException je) {
                    je.printStackTrace();
                }
            }
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            Log.d("PopulateSongs::dIB", "Total number of Songs from API : " + Integer.toString(jsonArray.length()));

            songs = new ArrayList<>();
            String songId = "";
            String songTitle= "";
            String songCoverImage= "";
            Integer noOfSongs = jsonArray.length();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    songId = jsonObject1.getString("id");
                    Log.d("PopulateSongs::dIB", "Song ID " + songId);

                    JSONObject snippetObject = jsonObject1.getJSONObject("snippet");
                    songTitle = snippetObject.getString("title");
                    Log.d("PopulateSongs::dIB", "Song Title " + songTitle);

                    songCoverImage = ((snippetObject.getJSONObject("thumbnails")).getJSONObject("default")).getString("url");
                    Log.d("PopulateSongs::dIB", "Song ImageURI " + songCoverImage);

                }
                catch (Exception e1) {
                    e1.printStackTrace();
                    noOfSongs--;
                    Log.d("PopulateSongs::dIB", "========== Error in this song, skipping ==========");
                    continue;
                }

                Song song = new Song(this.songListActivity, songId, songTitle, songCoverImage, noOfSongs);
                Log.d("PopulateSongs::dIB", "Added Song number : " + Integer.toString(i));
                songs.add(song);
            }

            Log.d("PopulateSongs::dIB", "================================ DONE Populating ================================");
            return songs;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Song> songs) {
        this.songListActivity.addSongs(songs);
    }
}
