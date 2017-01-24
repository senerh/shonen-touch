package dao.preferences;

import android.content.Context;

public class UserPreferences extends AbstractPreferencesDAO {

    private static final String PSEUDONYME_KEY = "dao.preferences.UserPreferences.PSEUDONYME_KEY";

    public UserPreferences(Context context) {
        super(context);
    }

    public String getPseudonyme() {
        return readPreferences(PSEUDONYME_KEY);
    }

    public void savePseudonyme(String pseudonyme) {
        savePreferences(PSEUDONYME_KEY, pseudonyme);
    }
}
