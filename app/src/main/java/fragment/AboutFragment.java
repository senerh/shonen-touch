package fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.shonen.shonentouch.R;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_about, container, false);

        getActivity().setTitle(R.string.menu_item_about);

        String aboutContentString = getString(R.string.about_content);
        WebView aboutContentWebView = (WebView) view.findViewById(R.id.about_content);
        aboutContentWebView.setBackgroundColor(Color.TRANSPARENT);

        aboutContentWebView.loadData(aboutContentString, "text/html; charset=utf-8", "utf-8");

        return view;
    }

}
