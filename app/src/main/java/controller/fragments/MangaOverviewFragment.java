package controller.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.senerh.shonentouch.R;
import model.adapters.OnItemClickListener;

public class MangaOverviewFragment extends Fragment implements View.OnClickListener, OnItemClickListener {

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                default :
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manga_overview, null, false);
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

    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter();

        getActivity().registerReceiver(mBroadcastReceiver, filter);

        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }


    @Override
    public void onMangaClick(View view, int position) {

    }
}
