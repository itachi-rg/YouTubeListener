package com.grohan.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by rg on 18-Jun-15.
 */
public class GetHtmlPage extends AsyncTask<Void, Void, String> {
    private String mUrl;
    private URL url;
    private HttpURLConnection urlConnection;

    GetHtmlPage(String mUrl) {
        this.mUrl = mUrl;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            url = new URL(mUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            BufferedReader is = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = is.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
        catch (MalformedURLException me) {
            Log.d("GetHtmlPage::dIB", "Malformed URL exception for URL : ");
            me.printStackTrace();
        }
        catch (IOException io) {
            Log.d("GetHtmlPage::dIB", "IOException");
            io.printStackTrace();
        }
        catch (Exception e) {
            Log.d("GetHtmlPage::dIB", "Exception");
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        return null;
    }
}
