package com.grohan.myapplication;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;

import org.apache.commons.lang3.StringUtils;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

/**
 * Created by rg on 31-May-15.
 */
public class DownloadFile extends AsyncTask<Void, Void, String> {
    private SongMediaPlayer activity;
    private String mUrl;
    private URL url;
    private HttpURLConnection urlConnection;

    DownloadFile(SongMediaPlayer activity, String url) {
        this.activity = activity;
        mUrl = url;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {


            /*String cacheDir = this.activity.getCacheDir().getAbsolutePath();
            Log.d("CACHE DIR", cacheDir);
            */
            String[] segments = mUrl.split("/");
            String songNameOnly = segments[segments.length-1];
            String encodedSong = (URLEncoder.encode(songNameOnly, "UTF-8")).replaceAll("\\+", "%20");

            String[] newSegments = Arrays.copyOf(segments, segments.length - 1);
            String urlWithoutSong = StringUtils.join(newSegments, "/");
            String songURL = urlWithoutSong+"/"+encodedSong;


            Log.d("DownloadFile::dIB","Song download URL :" + songURL);
            url = new URL(songURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            int totalSize = (urlConnection.getContentLength())/1024;
            Log.d("DownloadFile::dIB", "SONG size in kb " + Integer.toString(totalSize));

            ProgressBar progressBar = SongMediaPlayer.progressBar;
            progressBar.setProgress(0);
            progressBar.setMax(totalSize);

            BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());


            File sdCard = Environment.getExternalStorageDirectory();

            File playListDir = new File(sdCard.getAbsolutePath(), "Music");

            if(!playListDir.exists()) {
                playListDir.mkdir();
            }

            File songFile = new File(sdCard.getAbsolutePath(), "/Music/"+songNameOnly);

            FileOutputStream fileOutputStream = new FileOutputStream(songFile);
            byte[] buffer = new byte[1024];

            int bufferSize = 0;
            long downloadedSize = 0;
            while( (bufferSize = bufferedInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bufferSize);
                downloadedSize += bufferSize;
                int middle = (int)downloadedSize/1024;
                progressBar.setProgress(middle);
            }
            fileOutputStream.close();
            Log.d("DownloadFile::dIB", " Downloaded File size : " + Long.toString(songFile.length()));
            Log.d("DownloadFile::dIB", " Downloaded File location : " + songFile.getAbsolutePath());
            songFile.setExecutable(true);
            songFile.setReadable(true);
            songFile.setWritable(true);
            return songFile.getAbsolutePath();
        }
        catch (MalformedURLException me) {
            Log.d("DownloadFile::dIB", "Malformed URL exception for URL : " + mUrl);
            me.printStackTrace();
        }
        catch (IOException io) {
            Log.d("DownloadFile::dIB", "IOException");
            io.printStackTrace();
        }
        catch (Exception e) {
            Log.d("DownloadFile::dIB", "Exception");
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String output) {
         this.activity.playFileViaDisk(output);
    }
}
