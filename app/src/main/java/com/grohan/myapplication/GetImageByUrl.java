package com.grohan.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by rg on 31-May-15.
 */
public class GetImageByUrl extends AsyncTask<Void, Void, Bitmap> {

    Object object;
    String url;

    GetImageByUrl(Object object, String mUrl) {
        this.object = object;
        this.url = mUrl;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL imageUrl = new URL(url);
            return BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap output) {
        if (object instanceof PlayList) {
            ((PlayList)object).setBitmap(output);
            ((PlayList)object).updatePlaylistView();
        }
        else if (object instanceof Song) {
            ((Song)object).setBitmap(output);
            ((Song)object).updatePlaylistView();
        }
    }
}
