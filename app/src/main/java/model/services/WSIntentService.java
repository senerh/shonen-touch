package model.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Parcelable;
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
import java.util.List;
import java.util.Scanner;

import model.entities.Manga;


/**
 * Created by Thibaut SORIANO on 28/03/2017.
 */

public class WSIntentService extends IntentService {

    public static final String URL_SERVER = "http://senerh.xyz:8080/shonen-touch-api/";
    public static final String GET_ALL_MANGA = URL_SERVER + "mangas";

    public static final String PARAM_MANGAS_LIST = "mangasList";

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
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";

            List<Manga> mangas = new ArrayList<>();
            JSONArray tabManga = new JSONArray(result);

            for (int i = 0; i < tabManga.length(); i++) {
                JSONObject currentManga = tabManga.getJSONObject(i);
                mangas.add(new Manga(currentManga.getString("name"), currentManga.getString("slug")));
            }

            intent.putParcelableArrayListExtra(PARAM_MANGAS_LIST, (ArrayList<? extends Parcelable>) mangas);
            System.out.println("\n\n************** response ***************************\n" + result + "\n\n");
            sendBroadcast(intent);

            urlConnection.disconnect();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}