package dao.shonentouch;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import activity.FavoritesActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import dto.Manga;

public class MangaSTDAO extends AsyncTask<Void, Manga, List<Manga>> {

    private FavoritesActivity favoritesActivity;

    public MangaSTDAO(FavoritesActivity favoritesActivity) {
        this.favoritesActivity = favoritesActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        favoritesActivity.displayOnPreExecute();
    }

    @Override
    protected void onPostExecute(List<Manga> mangaList) {
        favoritesActivity.displayOnPostExecute(mangaList);
    }

    @Override
    protected List<Manga> doInBackground(Void... params) {
        List<Manga> mangaList = null;

        try {
            String string = callAPI();
            ObjectMapper objectMapper = new ObjectMapper();
            mangaList = objectMapper.readValue(string, objectMapper.getTypeFactory().constructCollectionType(List.class, Manga.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mangaList;
    }

    public String callAPI() {
        StringBuilder builder = new StringBuilder();

        try {
            URL samples = new URL("http://senerh.xyz:8080/shonen-touch-api/mangas");
            URLConnection yc = samples.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                builder.append(inputLine);
            in.close();
        } catch(MalformedURLException e ){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

}
