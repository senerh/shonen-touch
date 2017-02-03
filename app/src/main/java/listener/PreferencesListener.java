package listener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.shonen.shonentouch.R;

import activity.MainActivity;
import dao.preferences.UserPreferencesDAO;
import fragment.FavoriteFragment;
import fragment.PreferenceFragment;

public class PreferencesListener implements View.OnClickListener {

    private MainActivity mainActivity;
    private UserPreferencesDAO userPreferencesDAO;
    private int idButton;
    private PreferenceFragment preferenceFragment;


    public PreferencesListener(PreferenceFragment preferenceFragment, int idButton) {
        this.preferenceFragment = preferenceFragment;
        mainActivity = (MainActivity) preferenceFragment.getActivity();
        userPreferencesDAO = new UserPreferencesDAO(mainActivity.getBaseContext());
        this.idButton = idButton;
    }

    @Override
    public void onClick(View v) {
        switch (idButton) {
            case R.id.usernamePreference:
                onSetPseudonymeButtonClick();
                break;
            case R.id.favoritePreference:
                onFavoriteUpdateClick();
                break;
        }
    }

    private void onFavoriteUpdateClick() {
        mainActivity.switchFragment(new FavoriteFragment());
    }

    private void onSetPseudonymeButtonClick() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivity);
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.pseudonyme);
        edt.setText(userPreferencesDAO.getUsername());
        dialogBuilder.setTitle(R.string.username);

        dialogBuilder.setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                userPreferencesDAO.saveUsername(edt.getText().toString());
                preferenceFragment.updateUsername();
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();

        b.show();
    }
}
