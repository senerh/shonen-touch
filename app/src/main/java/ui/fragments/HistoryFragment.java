package ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.senerh.shonentouch.R;

public class HistoryFragment extends Fragment implements View.OnClickListener {
    public static HistoryFragment newInstance(int mangaId) {
        final HistoryFragment fragment = new HistoryFragment();
        final Bundle arguments = new Bundle();
        arguments.putInt("mangaId", mangaId);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manga_history, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            default :
                break;
        }
    }
}
