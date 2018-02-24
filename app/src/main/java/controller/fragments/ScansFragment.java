//package controller.fragments;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import io.github.senerh.shonentouch.R;
//
//public class ScansFragment extends Fragment implements View.OnClickListener {
//    public static ScansFragment newInstance(int mangaId) {
//        final ScansFragment fragment = new ScansFragment();
//        final Bundle arguments = new Bundle();
//        arguments.putInt("mangaId", mangaId);
//        fragment.setArguments(arguments);
//
//        return fragment;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_scans, null, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId())
//        {
//            default :
//                break;
//        }
//    }
//}
package controller.fragments;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import controller.activities.PageActivity;
import io.github.senerh.shonentouch.R;
import model.adapters.OnItemClickListener;
import model.adapters.ScanAdapter;
import model.database.ShonenTouchContract;
import model.entities.Scan;
import model.services.WSIntentService;

public class ScansFragment extends Fragment implements OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    // Loaders
    private static final int SCAN_LOADER = 1;
    private Cursor mCursor;

    public static final String ID_SCAN_LIST = "fragment.ScanFragment.ID_SCAN_LIST";
    public static final String ID_MANGA_PARCELABLE = "fragment.ScanFragment.ID_MANGA_PARCELABLE";
    public static final String ID_SCAN_PARCELABLE = "fragment.ScanFragment.ID_SCAN_PARCELABLE";

    private RecyclerView mRecyclerView;
    private ScanAdapter mAdapter;
    private int mMangaId;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case WSIntentService.GET_ALL_SCANS_FOR_MANGA:
                    List<Scan> scans = intent.getParcelableArrayListExtra(WSIntentService.PARAM_SCANS_LIST);

                    // persist all new scans in db
                    for (Scan scan : scans) {
                        boolean alreadyExists = false;

                        for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                            if (scan.getName().equals(mCursor.getString(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns.NAME)))) {
                                alreadyExists = true;
                            }
                        }

                        // persist
                        if (!alreadyExists) {
                            ContentValues newScan = new ContentValues();

                            newScan.put(ShonenTouchContract.ScanColumns.NAME, scan.getName());
                            newScan.put(ShonenTouchContract.ScanColumns.MANGA_ID, mMangaId);
                            newScan.put(ShonenTouchContract.ScanColumns.STATUS, Scan.Status.NOT_DOWNLOADED.name());

                            // persisting scan
                            getActivity().getApplicationContext().getContentResolver().insert(ShonenTouchContract.Scan.CONTENT_URI, newScan);
                        }
                    }
                    break;
                default :
                    break;
            }
        }
    };

    public static ScansFragment newInstance(int mangaId) {
        final ScansFragment fragment = new ScansFragment();
        final Bundle arguments = new Bundle();
        arguments.putInt("mangaId", mangaId);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scans, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mMangaId = getArguments().getInt("mangaId", -1);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_scans);
        mAdapter = new ScanAdapter(getContext(), this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(SCAN_LOADER, null, this);

        fetchScans();

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent myIntent = new Intent(getActivity(), PageActivity.class);

        Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns._ID + "=?", new String[]{ String.valueOf(mMangaId) }, null);
        if (c != null) {
            try {
                if (c.getCount() == 1) {
                    c.moveToFirst();
                    mCursor.moveToPosition(position);
                    switch (Scan.Status.valueOf(mCursor.getString(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns.STATUS)))) {
                        case NOT_DOWNLOADED:
                            Intent intent = new Intent(getActivity(), WSIntentService.class);
                            intent.setAction(WSIntentService.DOWNLOAD_PAGES_FOR_SCAN);
                            intent.putExtra(WSIntentService.PARAM_MANGA_SLUG, c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.SLUG)));
                            intent.putExtra(WSIntentService.PARAM_SCAN_ID, mCursor.getInt(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns._ID)));
                            getActivity().startService(intent);
                            break;
                        case DOWNLOAD_COMPLETE:
                            Intent intentPager = new Intent(getActivity(), PageActivity.class);
                            intentPager.putExtra(PageActivity.EXTRA_SCAN_ID, mCursor.getInt(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns._ID)));
                            startActivity(intentPager);
                            break;
                    }
//                    Bundle b = new Bundle();
//                    b.putParcelable(ID_MANGA_PARCELABLE, new dto.Manga(c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.SLUG)), c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.NAME))));
//                    mCursor.moveToPosition(position);
//                    b.putParcelable(ID_SCAN_PARCELABLE, new dto.Scan(mCursor.getString(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns.NAME))));
//                    List<dto.Scan> scanList = new ArrayList<>();
//                    for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
//                        scanList.add(new dto.Scan(mCursor.getString(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns.NAME))));
//                    }
//                    b.putParcelableArrayList(ID_SCAN_LIST, new ArrayList<>(scanList));
//                    myIntent.putExtras(b);
//                    startActivity(myIntent);
                }
            } finally {
                c.close();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == SCAN_LOADER) {
            return new CursorLoader(getContext().getApplicationContext(), ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns.MANGA_ID + "=?", new String[]{ String.valueOf(mMangaId)}, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == SCAN_LOADER) {
            mCursor = data;
            mAdapter.swapCursor(mCursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == SCAN_LOADER) {
            mCursor = null;
            mAdapter.swapCursor(mCursor);
        }
    }

    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter();

        filter.addAction(WSIntentService.GET_ALL_SCANS_FOR_MANGA);

        getActivity().registerReceiver(mBroadcastReceiver, filter);

        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    private void fetchScans() {
        final Intent intent = new Intent(getActivity(), WSIntentService.class);

        intent.setAction(WSIntentService.GET_ALL_SCANS_FOR_MANGA);
        Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns._ID + "=?", new String[]{ String.valueOf(mMangaId) }, null);
        if (c != null) {
            try {
                if (c.getCount() == 1) {
                    c.moveToFirst();
                    intent.putExtra(WSIntentService.PARAM_MANGA_SLUG, c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.SLUG)));
                    getActivity().startService(intent);
                }
            } finally {
                c.close();
            }

        }

    }
}
