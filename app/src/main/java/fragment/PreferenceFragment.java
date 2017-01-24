package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shonen.shonentouch.R;

import dao.preferences.UserPreferences;
import listener.PreferencesListener;


public class PreferenceFragment extends Fragment {

    private TextView pseudoTextView;

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
        pseudoTextView = (TextView) view.findViewById(R.id.setPseudonyme);
        pseudoTextView.setOnClickListener(
                new PreferencesListener(getActivity(), pseudoTextView.getId()));
        return view;
    }
}
