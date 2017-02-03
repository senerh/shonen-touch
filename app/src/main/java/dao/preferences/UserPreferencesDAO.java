package dao.preferences;

import android.content.Context;

import java.util.Random;

import dao.slack.ChatSlackDAO;

public class UserPreferencesDAO extends AbstractPreferencesDAO {

    private static final String USERNAME_KEY = "dao.preferences.UserPreferencesDAO.USERNAME_KEY";

    public UserPreferencesDAO(Context context) {
        super(context);
    }

    public String getUsername() {
        String username = readPreferences(USERNAME_KEY);
        if (username.equals(AbstractPreferencesDAO.NO_PREFERENCE) || username.equals(ChatSlackDAO.ADMIN_USERNAME)) {
            int n = new Random().nextInt(10000);
            username = "USER-" + n;
            saveUsername(username);
        }
        return username;
    }

    public void saveUsername(String username) {
        if (!username.equals(ChatSlackDAO.ADMIN_USERNAME) && 0 < username.length() && username.length() < 20) {
            savePreferences(USERNAME_KEY, username);
        }
    }
}
