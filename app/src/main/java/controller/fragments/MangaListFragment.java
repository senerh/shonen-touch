package controller.fragments;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import io.github.senerh.shonentouch.R;
import model.services.WSIntentService;

public class MangaListFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manga_list, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        getActivity().getApplicationContext().deleteDatabase("Plug.db");
//        mPlugsList = readPlugsInDb();
//        int nbRowsDeleted = getActivity().getApplicationContext().getContentResolver().delete(PlugContract.Measure.CONTENT_URI, null, null);
//        Date date = new Date(1483225200000l);
//        Date date = new Date(1483225200000l);
//        mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

//        zhengqin();
//        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        String merguez = wifiInfo.getBSSID();

        Button zhengqin = (Button) view.findViewById(R.id.meurguez);
        zhengqin.setOnClickListener(this);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.meurguez:
                Intent intent = new Intent(getActivity(), AddNewMangaFragment.class);
                startActivity(intent);
                break;
            default :
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.menu_refresh:
////                mSwipeRefreshLayout.setRefreshing(true);
//                refreshPlugs();
//                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter();

//        filter.addAction(WSIntentService.GET_PLUG);
//
//        getActivity().registerReceiver(mBroadcastReceiver, filter);

        super.onResume();
    }

    @Override
    public void onPause() {
//        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }


}
