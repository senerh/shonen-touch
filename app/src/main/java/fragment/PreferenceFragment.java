package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shonen.shonentouch.R;

import dao.preferences.UserPreferencesDAO;
import listener.PreferencesListener;

public class PreferenceFragment extends Fragment {

    private TextView textView;
    private UserPreferencesDAO userPreferencesDAO;

    public PreferenceFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_preference, container, false);

        getActivity().setTitle(R.string.menu_item_prefs);

        LinearLayout usernameLinearLayout = (LinearLayout) view.findViewById(R.id.usernamePreference);
        usernameLinearLayout.setOnClickListener(
                new PreferencesListener(this, usernameLinearLayout.getId()));

        textView = (TextView) view.findViewById(R.id.usernamePreferenceTextView);
        userPreferencesDAO = new UserPreferencesDAO(getContext());
        updateUsername();


        LinearLayout favoriteLinearLayout = (LinearLayout) view.findViewById(R.id.favoritePreference);
        favoriteLinearLayout.setOnClickListener(
                new PreferencesListener(this, favoriteLinearLayout.getId()));

        return view;
    }

    public void updateUsername() {
        textView.setText(userPreferencesDAO.getUsername());
    }
}
