package com.grohan.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by rg on 18-Jun-15.
 */
public class GetSongUrl extends AsyncTask<Void, Void, HashMap<String, String>> {
    private Activity activity;
    private String song;
    private String searchSong;
    private HttpURLConnection urlConnection;
    private HashMap<HashMap<String, String>, Double> songMap;
    private HashMap<String, String> sortedSongMap;

    public static String fckh;


    GetSongUrl(Activity activity, String Song) {
        this.activity = activity;
        song = Song;
    }

    GetSongUrl(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected HashMap<String, String> doInBackground(Void... params) {
        try {

            // have to get fchk from main page, then search song string with get request and get link;
            if (this.song == null) {
                String htmlPage = getHtmlPage("https://mp3skull.cr");
                Log.d("GetSongUrl::dIB", "HTML page content : " + htmlPage);
                Document doc = Jsoup.parse(htmlPage);
                Elements elements = doc.select("input[name=fckh]");
                Element element = elements.first();
                GetSongUrl.fckh = element.attr("value");
                Log.d("GetSongUrl::dIB", "fchk : " + fckh);
                return null;
            }

            Log.d("GetSongUrl::dIB", "================ Search and get Song URL ================");

            // String searchSong = ((this.song.replaceAll("[-+.:,)(&]|\\[|\\]","")).trim());
            String searchSong = Normalizer.normalize(this.song, Normalizer.Form.NFD).replaceAll("[^a-zA-Z]", " ");

            searchSong = searchSong.replaceAll("(?i)video", "");
            searchSong = searchSong.replaceAll("(?i)official", "");
            searchSong = searchSong.replaceAll("(?i)official video", "");
            searchSong = searchSong.replaceAll("(?i)official music video", "");
            searchSong = searchSong.replaceAll("(?i)explicit", "");
            searchSong = searchSong.replaceAll("(?i)ft", "");

            this.searchSong = searchSong;
            Log.d("GetSongUrl::dIB", "search song : " + searchSong);

            String songSearchString = searchSong.replaceAll(" +", "+");
            Log.d("GetSongUrl::dIB", "song search string : " + songSearchString);

            songMap = new HashMap<>();
            getUrlsFromMp3Skull(songSearchString);
            sortSongs();
            Log.d("GetSongUrl::Mp3Skull", "Hashmap size " + Integer.toString(this.sortedSongMap.size()));

            for (Map.Entry<String, String> songEntry : this.sortedSongMap.entrySet()) {
                Log.d("PlaySong::dIB", "Sorted hashmap song name " + songEntry.getKey());
                Log.d("PlaySong::dIB", "Sorted hashmap song value " + songEntry.getValue());
            }
            return this.sortedSongMap;

            /*String searchPage = getHtmlPage("https://mp3skull.cr/search_db.php?q=" + songSearchString + "&fckh=" + GetSongUrl.fckh);
            Document document = Jsoup.parse(searchPage);
            Element ele = (document.select("div.download_button").first()).select("a").first();
            Log.d("GetSongUrl::dIB", "song link : " + ele.attr("href"));
            return ele.attr("href");*/

        }

        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> output) {
        if (this.activity instanceof  SongMediaPlayer) {
            ((SongMediaPlayer) this.activity).playFileViaURL(output);
        }
    }

    String getHtmlPage(String mUrl) {
        try {
            //System.gc();
            URL url = new URL(mUrl);
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
        catch (Exception e) {
            Log.d("GetSongUrl::dIB", "Exception");
            e.printStackTrace();
        }
        return null;
    }

    Void getUrlsFromMp3Skull(String songSearchString) {
        Log.d("GetSongUrl::Mp3Skull", "================ MP3 SKULL ================");
        String searchPage = getHtmlPage("https://mp3skull.cr/search_db.php?q=" + songSearchString + "&fckh=" + GetSongUrl.fckh);
        Document document = Jsoup.parse(searchPage);

        // Get first 9 links
        Elements songElements = document.select("[class~=^show[1-9]$]");

        Log.d("GetSongUrl::Mp3Skull", "songElement size : " + Integer.toString(songElements.size()));


        for (Element songElement : songElements) {
            try {
                Element titleElement = songElement.getElementsByClass("mp3_title").first();
                Element hrefElement = songElement.getElementsByClass("download_button").first();
                String title = titleElement.text();
                String songUrl = (hrefElement.select("a")).attr("href");
                Log.d("GetSongUrl::Mp3Skull", "Title element : " + title);
                Log.d("GetSongUrl::Mp3Skull", "Href element : " + songUrl);

                Double similarity = LetterPairSimilarity.compareStrings(this.searchSong, title);
                Log.d("GetSongUrl::Mp3Skull", "Similarity : " + Double.toString(similarity));

                HashMap<String, String> titleUrlPair = new HashMap<>();
                titleUrlPair.put(title, songUrl);
                this.songMap.put(titleUrlPair, similarity);

            }
            catch (Exception e) {
                Log.d("GetSongUrl::Mp3Skull", "Exception ");
                e.printStackTrace();
            }
        }
        return null;
    }

    public void sortSongs() {
        this.sortedSongMap = new LinkedHashMap<String, String>();
        List list = new LinkedList(this.songMap.entrySet());

        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            Map.Entry<String, String> hashEntry = ((HashMap<String, String>)(entry.getKey())).entrySet().iterator().next();
            this.sortedSongMap.put(hashEntry.getKey(), hashEntry.getValue());
            Log.d("GetSongURL::sortSongs", "key : " + hashEntry.getKey());
            Log.d("GetSongURL::sortSongs", "url : " + hashEntry.getValue());
            Log.d("GetSongURL::sortSongs", "Value : " + Double.toString(((Double)entry.getValue())));
        }
    }
}
