package dao.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dto.Manga;

public class PreferencesDAO {
    SharedPreferences preferences;
    private Context context;

    public PreferencesDAO(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void savePreferences(String key, String value) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String readPreferences(String key) {
        return preferences.getString(key, "No preferences");
    }

    public List<Manga> jsonToManga(String mangaListString){
        List<Manga> mangaList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            mangaList = objectMapper.readValue(mangaListString, TypeFactory.defaultInstance().constructCollectionType(List.class, Manga.class));
        } catch (IOException e) {
            Log.e(getClass().getName(), "Error while trying to get list of mangas from the following json <~" + mangaListString + "~>");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
        return mangaList;
    }

    public String mangaToJson (List<Manga> mangaList) {
        String mangaListString = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            mangaListString = objectMapper.writeValueAsString(mangaList);
        } catch (JsonProcessingException e) {
            Log.e(getClass().getName(), "Error while trying to get json from the following list of mangas <~" + mangaList + "~>");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
        return mangaListString;
    }
}