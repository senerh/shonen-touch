package model.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Created by Thibaut SORIANO on 28/03/2017.
 */

public class WSIntentService extends IntentService {

    public static final String URL_SERVER = "http://senerh.xyz:8080/shonen-touch-api/";
    public static final String GET_ALL_MANGA = URL_SERVER + "mangas";

    public static final String PARAM_MANGA_NAMES = "mangaNames";
    public static final String PARAM_MANGA_SLUGS = "mangaSlugs";

    public WSIntentService() {
        super("WSIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        switch (intent.getAction()) {
            case GET_ALL_MANGA:
                getAllManga(GET_ALL_MANGA);
                break;
            default:
                break;
        }
    }

    public void getAllManga(String u) {
        final Intent intent = new Intent(GET_ALL_MANGA);

        try {
            URL url = new URL(u);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestProperty("Content-Type","application/json");
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
//            intent.putExtra("Response", result);

            ArrayList<String> names = new ArrayList<>();
            ArrayList<String> slugs = new ArrayList<>();
            JSONArray tabManga = new JSONArray(result);

            for (int i = 0; i < tabManga.length(); i++) {
                JSONObject currentManga = tabManga.getJSONObject(i);
                slugs.add(currentManga.getString("slug"));
                names.add(currentManga.getString("name"));
            }

            intent.putStringArrayListExtra(PARAM_MANGA_NAMES, names);
            intent.putStringArrayListExtra(PARAM_MANGA_SLUGS, slugs);
            System.out.println("\n\n************** response ***************************\n" + result + "\n\n");
            sendBroadcast(intent);

            urlConnection.disconnect();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}