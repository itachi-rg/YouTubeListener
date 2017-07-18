package com.grohan.myapplication;

import android.media.AudioManager;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rg on 18-July-15.
 */
public class PlaySong extends AsyncTask<Void, Void, String> {
    private SongMediaPlayer activity;
    private String songUrl;
    private HashMap<String, String> sortedSongMap;

    PlaySong(SongMediaPlayer activity, HashMap<String, String> songMap) {
        this.activity = activity;
        this.sortedSongMap = songMap;
    }

    @Override
    protected String doInBackground(Void... params) {
        for (Map.Entry<String, String> songEntry : this.sortedSongMap.entrySet()) {
            try {
                this.songUrl = songEntry.getValue();
                String[] segments = this.songUrl.split("/");
                String songNameOnly = segments[segments.length - 1];
                String encodedSong = (URLEncoder.encode(songNameOnly, "UTF-8")).replaceAll("\\+", "%20");

                String[] newSegments = Arrays.copyOf(segments, segments.length - 1);
                String urlWithoutSong = StringUtils.join(newSegments, "/");
                String songURL = urlWithoutSong + "/" + encodedSong;
                Log.d("PlaySong::dIB", "Play song URL : " + songURL);

                boolean statusCode = playFileViaURL(songURL);

                if(!statusCode) {
                    Log.d("PlaySong::dIB", "Could not play Song, trying another link");
                }
                else {
                    return this.songUrl;
                }

            } catch (IOException io) {
                Log.d("PlaySong::dIB", "IOException");
                Log.d("PlaySong::dIB", "Could not play Song, trying another link");
                io.printStackTrace();
            } catch (Exception e) {
                Log.d("PlaySong::dIB", "Exception");
                Log.d("PlaySong::dIB", "Could not play Song, trying another link");
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String songurl) {
        this.activity.songStatus(songurl);
    }

    public boolean playFileViaURL(String songURL) {
        System.gc();
        boolean pingable = ping(songURL);
        if (!pingable) {
            return false;
        }
        Log.d("playFileFromURL", songURL);
        try {
            SongMediaPlayer.mediaPlayer.reset();
            SongMediaPlayer.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            SongMediaPlayer.mediaPlayer.setDataSource(songURL);
            SongMediaPlayer.mediaPlayer.prepare();
            SongMediaPlayer.mediaPlayer.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean ping(String URL) {
        HttpURLConnection httpURLConnection = null;
        try {
            Log.d("PlaySong::ping", "Trying to ping song URL location");
            httpURLConnection = (HttpURLConnection) new URL(URL).openConnection();
            httpURLConnection.setRequestMethod("HEAD");
            httpURLConnection.setConnectTimeout(5000);
            int responseCode = httpURLConnection.getResponseCode();
            Log.d("PlaySong::ping", "SongURL status code : " + Integer.toString(responseCode));
            if (responseCode != 200 && responseCode != 403) {
                Log.d("PlaySong::ping", "Not Reachable FALSE");
                return false;
            }
            Log.d("PlaySong::ping", "Reachable TRUE");
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d("PlaySong::ping", "Not Reachable FALSE");
            return false;
        }
        finally {
            try {
                httpURLConnection.disconnect();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
