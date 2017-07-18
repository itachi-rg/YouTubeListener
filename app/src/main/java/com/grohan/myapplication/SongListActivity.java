package com.grohan.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SongListActivity extends Activity {

    static String token;
    static String playListid;
    public static String nextPageToken = "";
    public static int totalSongs = 0;
    private boolean loadNextPage = true;

    String imageCacheURI;
    List<Song> songs;
    SongListAdapter songListAdapter;
    ProgressBar spinner;
    static String previousSong;

    MediaPlayer mediaPlayer;

    public void stopSpinner() {
        this.spinner.setVisibility(View.GONE);
    }

    public void startSpinner() {
        this.spinner.setVisibility(View.VISIBLE);
    }

    public void getSongList() {
        new GetApiResult(this, "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=" + SongListActivity.playListid, SongListActivity.token).execute();
    }

    public void addSong(Song newSong) {
        this.songs.add(newSong);
    }

    public void updateSonglistView() {
        this.songListAdapter.notifyDataSetChanged();
    }

    public void addSongs(List<Song> newSongs) {
        this.songs.addAll(newSongs);
        Log.d("SongListAct::addSongs", "Added total of " + Integer.toString(songs.size()) + " Songs");
        this.songListAdapter.notifyDataSetChanged();
        this.loadNextPage = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);

        if (SongMediaPlayer.mediaPlayer != null) {
            this.mediaPlayer = SongMediaPlayer.mediaPlayer;
        }

        Log.d("SongListAct::onCreate", "onCreate");
        this.spinner = (ProgressBar) findViewById(R.id.progressBar2);
        startSpinner();

        songs = new ArrayList<>();
        songListAdapter = new SongListAdapter(this, songs);
        final ListView songListList = (ListView) findViewById(R.id.songListView);
        songListList.setAdapter(songListAdapter);

        songListList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = ((TextView) view.findViewById(R.id.each_song_name)).getText().toString();
                Log.d("SongListAct::onCreate", "Clicked on Song : " + songName);
                ImageView imageView = (ImageView) view.findViewById(R.id.each_song_cover);
                Bitmap songImage = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                SongListActivity.this.imageCacheURI = createImageFromBitmap(songImage);
                Intent intent = new Intent(SongListActivity.this, SongMediaPlayer.class);
                intent.putExtra("SongName", songName);
                intent.putExtra("ImageURI", SongListActivity.this.imageCacheURI);
                startActivity(intent);
            }
        });

        songListList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItemIndex, int visibleItemCount, int totalItemCount) {
                Log.d("SongListAct::onScrollL", "first visible item index: "+firstVisibleItemIndex+"visible item count: "+visibleItemCount+"total item count : "+totalItemCount);
                if ((totalItemCount < SongListActivity.totalSongs)
                        &&(firstVisibleItemIndex + visibleItemCount > (totalItemCount / 2))
                        && (SongListActivity.totalSongs > 50) && SongListActivity.this.loadNextPage) {
                    new GetApiResult(SongListActivity.this,
                            "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&pageToken="+SongListActivity.nextPageToken+"&maxResults=50&playlistId=" + SongListActivity.playListid, SongListActivity.token).execute();
                    SongListActivity.this.loadNextPage = false;
                }

            }
        });

        if (getIntent().getStringExtra("PlaylistId") != null) {
            SongListActivity.playListid = getIntent().getStringExtra("PlaylistId");
            SongListActivity.token = getIntent().getStringExtra("Token");
        }
        else {
            Log.d("SongListAct::onCreate", "PlayList ID : " + SongListActivity.playListid);
            Log.d("SongListAct::onCreate", "Token : " + SongListActivity.token);
        }

        Log.d("SongListAct::onCreate", "PlayList ID : " + SongListActivity.playListid);
        Log.d("SongListAct::onCreate", "Token : " + SongListActivity.token);

        getSongList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.song_list, menu);
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

    public String createImageFromBitmap(Bitmap bitmap) {

        Log.d("SongListAct::CacheImage", "Caching Image for Song");

        String cacheDir = SongListActivity.this.getCacheDir().getAbsolutePath();
        Log.d("SongListAct::CacheImage", "CACHE DIR" + cacheDir);
        String fileName = "currentSong.jpg";//no .png or .jpg needed
        File imageFile = new File(cacheDir, fileName);
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = new FileOutputStream(imageFile);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            Log.d("SongListAct::CacheImage", "Exception while caching");
            e.printStackTrace();
        }
        Log.d("SongListAct::CacheImage", "Cached Image Path : " + imageFile.getAbsolutePath());
        return imageFile.getAbsolutePath();
    }
}
