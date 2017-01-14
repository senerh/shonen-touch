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

import dao.shonentouch.UtilsShonentouchDAO;
import dto.Manga;

public abstract class AbstractPreferencesDAO {

    public static final String NO_PREFERENCE = "No preference";

    private SharedPreferences sharedPreferences;

    public AbstractPreferencesDAO(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    protected void savePreferences(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    protected String readPreferences(String key) {
        return sharedPreferences.getString(key, NO_PREFERENCE);
    }

}