package ui.fragments;

import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import model.adapters.OnItemLongClickListener;
import model.services.HeavyActionsIntentService;
import ui.activities.PageActivity;
import io.github.senerh.shonentouch.R;
import model.adapters.OnItemClickListener;
import model.adapters.ScanAdapter;
import model.database.ShonenTouchContract;
import model.entities.Scan;
import model.services.WSIntentService;
import ui.dialogs.AlertDialogFragment;

public class ScansFragment extends Fragment implements OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>, OnItemLongClickListener {
    // Loaders
    private static final int SCAN_LOADER = 1;
    private Cursor mCursor;

    private static final int REQUEST_DELETE_SCAN_DIALOG = 0;
    private static final int REQUEST_STOP_DOWNLOAD_DIALOG = 1;
    private static final int REQUEST_RESUME_OR_DELETE_DOWNLOAD_DIALOG = 2;

    public static final String EXTRA_SCAN_ID = "EXTRA_SCAN_ID";
    public static final String EXTRA_MANGA_ID = "EXTRA_MANGA_ID";

    private ProgressBar mEmptyStateProgressBar;
    private TextView mEmptyStateTextView;
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
                    mEmptyStateProgressBar.setVisibility(View.GONE);
                    mEmptyStateTextView.setVisibility(View.GONE);
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
        mEmptyStateProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar_empty_state);
        mEmptyStateTextView = (TextView) view.findViewById(R.id.text_view_empty_state);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_scans);
        mAdapter = new ScanAdapter(getContext(), this, this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(SCAN_LOADER, null, this);

        Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns.MANGA_ID + "=?", new String[]{ String.valueOf(mMangaId) }, null);
        if (c != null) {
            try {
                if (c.getCount() >= 1) {
                    mEmptyStateProgressBar.setVisibility(View.GONE);
                    mEmptyStateTextView.setVisibility(View.GONE);
                } else if (c.getCount() == 0) {
                    fetchScans();
                }
            } finally {
                c.close();
            }
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(View view, int position) {
        Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns._ID + "=?", new String[]{ String.valueOf(mMangaId) }, null);
        if (c != null) {
            try {
                if (c.getCount() == 1) {
                    c.moveToFirst();
                    mCursor.moveToPosition(position);
                    Intent intent = new Intent();
                    switch (Scan.Status.valueOf(mCursor.getString(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns.STATUS)))) {
                        case NOT_DOWNLOADED:
                            intent = new Intent(getActivity(), WSIntentService.class);
                            intent.setAction(WSIntentService.DOWNLOAD_PAGES_FOR_SCAN);
                            intent.putExtra(WSIntentService.PARAM_MANGA_SLUG, c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.SLUG)));
                            intent.putExtra(WSIntentService.PARAM_SCAN_ID, mCursor.getInt(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns._ID)));
                            getActivity().startService(intent);
                            break;
                        case DOWNLOAD_COMPLETE:
                            intent = new Intent(getActivity(), PageActivity.class);
                            intent.putExtra(PageActivity.EXTRA_SCAN_ID, mCursor.getInt(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns._ID)));
                            startActivity(intent);
                            break;
                        case DOWNLOAD_STOPPED:
                            intent.putExtra(EXTRA_SCAN_ID, mCursor.getInt(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns._ID)));
                            intent.putExtra(EXTRA_MANGA_ID, mMangaId);
                            AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.dialog_resume_or_delete_download_title), getString(R.string.dialog_resume_or_delete_download_message, mCursor.getString(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns.NAME))),
                                    intent, true, true, true, R.string.dialog_resume_or_delete_download_delete_button, R.string.dialog_resume_or_delete_download_resume_button);
                            alertDialogFragment.setTargetFragment(this, REQUEST_RESUME_OR_DELETE_DOWNLOAD_DIALOG);
                            alertDialogFragment.show(getFragmentManager(), AlertDialogFragment.TAG);
                            break;
                        case DOWNLOAD_IN_PROGRESS:
                            alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.dialog_stop_download_title), getString(R.string.dialog_stop_download_message, mCursor.getString(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns.NAME))), intent, true, true);
                            alertDialogFragment.setTargetFragment(this, REQUEST_STOP_DOWNLOAD_DIALOG);
                            alertDialogFragment.show(getFragmentManager(), AlertDialogFragment.TAG);
                            break;
                    }
                }
            } finally {
                c.close();
            }
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns._ID + "=?", new String[]{ String.valueOf(mMangaId) }, null);
        if (c != null) {
            try {
                if (c.getCount() == 1) {
                    c.moveToFirst();
                    mCursor.moveToPosition(position);
                    Intent intent = new Intent();
                    AlertDialogFragment alertDialogFragment;
                    switch (Scan.Status.valueOf(mCursor.getString(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns.STATUS)))) {
                        case DOWNLOAD_COMPLETE:
                            intent.putExtra(EXTRA_SCAN_ID, mCursor.getInt(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns._ID)));
                            alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.dialog_delete_scan_title), getString(R.string.dialog_delete_scan_message, mCursor.getString(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns.NAME))), intent, true, true);
                            alertDialogFragment.setTargetFragment(this, REQUEST_DELETE_SCAN_DIALOG);
                            alertDialogFragment.show(getFragmentManager(), AlertDialogFragment.TAG);
                            break;
                    }
                }
            } finally {
                c.close();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_DELETE_SCAN_DIALOG:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        int scanId = data.getIntExtra(EXTRA_SCAN_ID, -1);
                        Intent intent = new Intent(getActivity(), HeavyActionsIntentService.class);
                        intent.setAction(HeavyActionsIntentService.DELETE_SCAN_PAGES);
                        intent.putExtra(HeavyActionsIntentService.EXTRA_SCAN_ID, scanId);
                        getActivity().startService(intent);
                        break;
                }
                break;
            case REQUEST_STOP_DOWNLOAD_DIALOG:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        WSIntentService.shouldContinue = false;
                        break;
                }
                break;
            case REQUEST_RESUME_OR_DELETE_DOWNLOAD_DIALOG:
                int scanId, mangaId;
                Cursor c;
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // resume download
                        mangaId = data.getIntExtra(EXTRA_MANGA_ID, -1);
                        c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns._ID + "=?", new String[]{ String.valueOf(mangaId) }, null);
                        if (c != null) {
                            try {
                                if (c.getCount() == 1) {
                                    c.moveToFirst();
                                    scanId = data.getIntExtra(EXTRA_SCAN_ID, -1);
                                    Intent intent = new Intent(getActivity(), WSIntentService.class);
                                    intent.setAction(WSIntentService.DOWNLOAD_PAGES_FOR_SCAN);
                                    intent.putExtra(WSIntentService.PARAM_MANGA_SLUG, c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.SLUG)));
                                    intent.putExtra(WSIntentService.PARAM_SCAN_ID, scanId);
                                    getActivity().startService(intent);
                                }
                            } finally {
                                c.close();
                            }
                        }
                        break;
                    case AlertDialogFragment.RESULT_NEUTRAL:
                        // delete all pages of this scan
                        scanId = data.getIntExtra(EXTRA_SCAN_ID, -1);
                        Intent intent = new Intent(getActivity(), HeavyActionsIntentService.class);
                        intent.setAction(HeavyActionsIntentService.DELETE_SCAN_PAGES);
                        intent.putExtra(HeavyActionsIntentService.EXTRA_SCAN_ID, scanId);
                        getActivity().startService(intent);
                        break;
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
