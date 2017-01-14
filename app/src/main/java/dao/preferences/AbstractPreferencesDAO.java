package dao.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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