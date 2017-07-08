package controller.fragments;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.github.senerh.shonentouch.R;
import model.adapters.MangaAdapter;
import model.services.WSIntentService;

public class AddNewMangaFragment extends Fragment implements View.OnClickListener {
    private RecyclerView mMangaRecyclerView;
    private MangaAdapter mMangaAdapter;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case WSIntentService.GET_ALL_MANGA:
                    break;
                default :
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_manga, null, false);
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

        mMangaRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_manga);
        final Intent intent = new Intent(getActivity(), WSIntentService.class);

        intent.setAction(WSIntentService.GET_ALL_MANGA);
        getActivity().startService(intent);
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

        filter.addAction(WSIntentService.GET_ALL_MANGA);

        getActivity().registerReceiver(mBroadcastReceiver, filter);

        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }


}
