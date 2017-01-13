package dao.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dto.Manga;

import static android.R.id.list;

/**
 * Created by Franck on 06/01/2017.
 */

public class PreferencesDAO {
    SharedPreferences preferences;
    private Context context;

    public PreferencesDAO(Context context){
        this.context=context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void savePreferences(String key, String value) {

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();

    }

    public String readPreferences(String key) {
        String value = preferences.getString(key, "No preferences");
        return value;
    }

    public List<Manga> jsonToManga(String favorisJson){
        ArrayList<Manga> listeMangas= new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            listeMangas = objectMapper.readValue(favorisJson,
                    TypeFactory.defaultInstance().constructCollectionType(List.class,
                            Manga.class));
        } catch (IOException e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
        return listeMangas;
    }

    public String mangaToJson (List<Manga> favorisManga) {
        String jsonMangas = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            jsonMangas=objectMapper.writeValueAsString(favorisManga);
        } catch (JsonProcessingException e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
        return jsonMangas;
    }
}