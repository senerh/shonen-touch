package listener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.shonen.shonentouch.R;

import activity.MainActivity;
import dao.preferences.UserPreferences;


public class PreferencesListener implements View.OnClickListener {

    private Activity activity;
    private UserPreferences userPreferences;
    private int idButton;


    public PreferencesListener(Activity activity, int idButton) {
        this.activity = activity;
        userPreferences = new UserPreferences(activity.getBaseContext());
        this.idButton = idButton;
    }

    @Override
    public void onClick(View v) {
        switch (idButton) {
            case R.id.setPseudonyme:
                onSetPseudonymeButtonClick();
                break;
        }
    }

    private void onSetPseudonymeButtonClick(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.pseudonyme);
        edt.setText(userPreferences.getPseudonyme());
        dialogBuilder.setTitle(R.string.titlePseudonyme);
        dialogBuilder.setMessage(R.string.messagePseudonyme);

        dialogBuilder.setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                userPreferences.savePseudonyme(edt.getText().toString());
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
