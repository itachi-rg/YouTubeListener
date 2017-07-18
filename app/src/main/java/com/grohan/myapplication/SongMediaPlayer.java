package com.grohan.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;


public class SongMediaPlayer extends Activity {

    public static MediaPlayer mediaPlayer = new MediaPlayer();
    String songUrl;
    String songName;

    public static ProgressBar progressBar;
    Bitmap songCover;

    public void playFileViaDisk(String file) {
        SongMediaPlayer.progressBar.setVisibility(View.GONE);
        Log.d("SMP::playFileViaURL", "File location : " + file);
        try {
            Toast.makeText(this, "Download complete !", Toast.LENGTH_LONG).show();

        /*
            SongMediaPlayer.mediaPlayer = new MediaPlayer();
            SongMediaPlayer.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            SongMediaPlayer.mediaPlayer.setDataSource(file);
            SongMediaPlayer.mediaPlayer.prepare();
            SongMediaPlayer.mediaPlayer.start();
            */
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.gc();
    }

    public void songStatus(String songurl) {
        if (songurl != null) {
            Toast.makeText(this, "Playing Song", Toast.LENGTH_LONG).show();
            this.songUrl = songurl;
        }
        else {
            Toast.makeText(this, "Cannot Play Song", Toast.LENGTH_LONG).show();
        }
    }

    public void playFileViaURL(HashMap<String, String> sortedSongMap) {
        Log.d("SMP::playFileViaURL", "Play any link in hash map");
        new PlaySong(this, sortedSongMap).execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_media_player);
        this.songName = getIntent().getStringExtra("SongName");

        new GetSongUrl(SongMediaPlayer.this, this.songName).execute();

        Log.d("SMP::onCreate", "Activity begins");

        final ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);

        imageButton.setImageResource(R.drawable.pause1);
        try {
            File imageFile = new File(getIntent().getStringExtra("ImageURI"));
            this.songCover = BitmapFactory.decodeStream(new FileInputStream(imageFile));
            ImageView coverImage = (ImageView) findViewById(R.id.media_cover);
            coverImage.setImageBitmap(this.songCover);
        }
        catch (Exception e) {
            Log.d("SMP::onCreate", "Exception in setting song cover Image");
            e.printStackTrace();
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SongMediaPlayer.mediaPlayer.isPlaying()) {
                    SongMediaPlayer.mediaPlayer.pause();
                    imageButton.setImageResource(R.drawable.play1);
                } else {
                    SongMediaPlayer.mediaPlayer.start();
                    imageButton.setImageResource(R.drawable.pause1);
                }
            }
        });

        SongMediaPlayer.progressBar = (ProgressBar) findViewById(R.id.downloadProgress);

        TextView downloadButton = (TextView) findViewById(R.id.downloadText);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SongMediaPlayer.progressBar.setVisibility(View.VISIBLE);
                new DownloadFile(SongMediaPlayer.this, SongMediaPlayer.this.songUrl).execute();
                Toast.makeText(SongMediaPlayer.this, "Started Download", Toast.LENGTH_LONG).show();

            }
        });
        Log.d("SMP", "Activity ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_song_media_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
