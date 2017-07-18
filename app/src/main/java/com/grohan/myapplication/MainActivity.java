package com.grohan.myapplication;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    //GUI
    private ProgressBar spinner;

    //YOUTUBE setting
    String token;
    String mEmail; // Received from newChooseAccountIntent(); passed to getToken()

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private final static String Youtube_SCOPES = "https://www.googleapis.com/auth/youtube" + " " +
            "https://www.googleapis.com/auth/youtube.force-ssl" + " " +
            "https://www.googleapis.com/auth/youtube.readonly" + " " +
            "https://www.googleapis.com/auth/youtube.upload" + " " +
            "https://www.googleapis.com/auth/youtubepartner" + " " +
            "https://www.googleapis.com/auth/youtubepartner-channel-audit";
    private final static String GooglePlus_SCOPE = "https://www.googleapis.com/auth/plus.login";
    private final static String SCOPE = "oauth2:"+Youtube_SCOPES+" "+GooglePlus_SCOPE;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

    //MUSIC Objects
    private PlayListAdapter playListAdapter;
    private List<PlayList> playLists;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        startSpinner();

        List<PlayList> playLists = new ArrayList<>();
        playListAdapter = new PlayListAdapter(this, playLists);
        final GridView playListList = (GridView) findViewById(R.id.playlist_list);
        playListList.setAdapter(playListAdapter);
        playListList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, SongListActivity.class);
                Log.d("MA::onCreate", "Playlist ID : " + ((TextView) view.findViewById(R.id.each_playlist_id)).getText().toString());
                Log.d("MA::onCreate", "Token for API request : " + MainActivity.this.token);

                intent.putExtra("PlaylistId", ((TextView) view.findViewById(R.id.each_playlist_id)).getText().toString());
                intent.putExtra("Token", MainActivity.this.token);

                startActivity(intent);
            }
        });

        // Get current username so that his playlists can be displayed
        getUsername();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void getUsername() {
        if (mEmail == null) {
            pickUserAccount();
        } else {
            new GetUsernameTask(MainActivity.this, mEmail, SCOPE).execute();

        }
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token
                getUsername();
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(this, "Cannot proceed without an account", Toast.LENGTH_SHORT).show();
                getUsername();
            }
        } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            // Receiving a result that follows a GoogleAuthException, try auth again
            getUsername();
        }
    }

    public void stopSpinner() {
        this.spinner.setVisibility(View.GONE);
    }

    public void startSpinner() {
        this.spinner.setVisibility(View.VISIBLE);
    }

    public void updatePlaylistView() {
        this.playListAdapter.notifyDataSetChanged();
    }

    public  void setToken(String tokenValue) {
        this.token = tokenValue;
        Log.d("MA::setToken", "Token value for Youtube apis : " + token);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetApiResult(MainActivity.this, "https://www.googleapis.com/youtube/v3/playlists?part=snippet&mine=true", token).execute();
            }
        });
    }


    public void handleException(final Exception e) {
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            MainActivity.this,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }

    public void playListOutput(String playListObject) {
        try {
            JSONObject wholeJsonObject = new JSONObject(playListObject);

            JSONArray jsonArray = wholeJsonObject.getJSONArray("items");
            Log.d("MA::playListOutput", "length of json array " + Integer.toString(jsonArray.length()));

            playLists = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String playListId = jsonObject.getString("id");
                    JSONObject snippetObject = jsonObject.getJSONObject("snippet");
                    String playListTitle = snippetObject.getString("title");
                    String playListCoverImage = ((snippetObject.getJSONObject("thumbnails")).getJSONObject("high")).getString("url");

                    Log.d("MA::playListOutput", "Playlist id " + playListId);
                    Log.d("MA::playListOutput", "Playlist title " + playListTitle);
                    Log.d("MA::playListOutput", "Playlist image url " + playListCoverImage);

                    PlayList playList = new PlayList(this, playListId, playListTitle, playListCoverImage, jsonArray.length());
                    playLists.add(playList);
                    playListAdapter.add(playList);

                }
                catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

            }
            Log.d("MA::playListOutput", "MA done");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
