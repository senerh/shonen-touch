package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.senerh.shonentouch.R;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_about, container, false);

        getActivity().setTitle(R.string.menu_item_about);

        TextView authorsTextView = (TextView) view.findViewById(R.id.about_authors);
        authorsTextView.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

}
