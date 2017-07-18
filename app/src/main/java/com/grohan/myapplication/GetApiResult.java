package com.grohan.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by rg on 31-May-15.
 */
public class GetApiResult extends AsyncTask<Void, Void, String> {
    private Activity activity;
    private String mUrl;
    private URL url;
    private HttpURLConnection urlConnection;
    private String accessToken;

    GetApiResult(Activity activity, String url) {
        this.activity = activity;
        mUrl = url;
    }

    GetApiResult(Activity activity, String url, String mAccessToken) {
        this.activity = activity;
        mUrl = url;
        accessToken = mAccessToken;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            url = new URL(mUrl);
            Log.d("GetApiResult::dIB", "URL is " + mUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (accessToken != null) {
                Log.d("GetApiResult::dIB", "Token not null ");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
            }
            urlConnection.connect();
            BufferedReader is = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = is.readLine()) != null) {
                sb.append(line);
            }
            Log.d("GetApiResult::dIB", "HTML page source \n" + sb.toString());

            return sb.toString();
        }
        catch (MalformedURLException me) {
            Log.d("GetApiResult::dIB", "Malformed URL exception for URL : " + mUrl);
            me.printStackTrace();
        }
        catch (IOException io) {
            Log.d("GetApiResult::dIB", "IOException");
            io.printStackTrace();
        }
        catch (Exception e) {
            Log.d("GetApiResult::dIB", "Exception");
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String output) {
        if (activity instanceof  MainActivity) {
            (((MainActivity) activity)).playListOutput(output);
        }
        else if (activity instanceof SongListActivity) {
            new PopulateSongs((SongListActivity) activity, output).execute();
        }
    }
}
