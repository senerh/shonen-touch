package ui.fragments;

import android.app.Activity;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import io.github.senerh.shonentouch.R;
import model.adapters.OnItemClickListener;
import model.adapters.OnItemLongClickListener;
import model.adapters.ScanAdapter;
import model.database.ShonenTouchContract;
import model.entities.Scan;
import model.services.HeavyActionsIntentService;
import model.services.WSIntentService;
import ui.activities.PageActivity;
import ui.dialogs.AlertDialogFragment;

public class ScansFragment extends Fragment implements OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>, OnItemLongClickListener, SearchView.OnQueryTextListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    // Loaders
    private static final int SCAN_LOADER = 1;
    private Cursor mCursor;

    private static final int REQUEST_DELETE_SCAN_DIALOG = 0;
    private static final int REQUEST_STOP_DOWNLOAD_DIALOG = 1;
    private static final int REQUEST_RESUME_OR_DELETE_DOWNLOAD_DIALOG = 2;

    public static final String EXTRA_SCAN_ID = "EXTRA_SCAN_ID";
    public static final String EXTRA_MANGA_ID = "EXTRA_MANGA_ID";

    private ProgressBar mLoadingProgressBar;
    private TextView mLoadingTextView, mEmptyStateTextView;
    private ScanAdapter mAdapter;
    private SearchView mScansSearchView;
    private int mMangaId;
    private String mCursorFilter;
    private CoordinatorLayout mSnackbarCoordinatorLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Snackbar mSnackbar;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Cursor c;
            switch (intent.getAction()) {
                case WSIntentService.GET_ALL_SCANS_FOR_MANGA:
                    switch (intent.getIntExtra(WSIntentService.EXTRA_RESULT_CODE, 0)) {
                        case WSIntentService.RESULT_OK:
                            c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns.MANGA_ID + "=?", new String[]{ String.valueOf(mMangaId) }, null);
                            if (c != null) {
                                try {
                                    List<Scan> scans = intent.getParcelableArrayListExtra(WSIntentService.PARAM_SCANS_LIST);

                                    // persist all new scans in db
                                    for (Scan scan : scans) {
                                        boolean alreadyExists = false;

                                        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                                            if (scan.getName().equals(c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.NAME)))) {
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

                                    ContentValues updatedManga = new ContentValues();
                                    updatedManga.put(ShonenTouchContract.MangaColumns.LAST_SCAN, scans.get(scans.size() - 1).getName());
                                    getActivity().getContentResolver().update(ShonenTouchContract.Manga.CONTENT_URI, updatedManga, ShonenTouchContract.MangaColumns._ID + "=?", new String[]{String.valueOf(mMangaId)});

                                    mScansSearchView.setVisibility(View.VISIBLE);
                                    ((AppCompatActivity) getActivity()).getSupportActionBar().setLogo(null);
                                    if (mSwipeRefreshLayout.isRefreshing()) {
                                        mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_scans_received), Snackbar.LENGTH_LONG);

                                        mSnackbar.show();
                                    }
                                } finally {
                                    c.close();
                                }
                            }
                            break;
                        case WSIntentService.RESULT_ERROR_NO_INTERNET:
                            mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_no_internet), Snackbar.LENGTH_LONG);

                            mSnackbar.show();
                            break;
                        case WSIntentService.RESULT_ERROR_BAD_RESPONSE:
                        case WSIntentService.RESULT_ERROR_TIMEOUT:
                            mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_server_error), Snackbar.LENGTH_LONG);

                            mSnackbar.show();
                            break;
                        default:
                            break;
                    }
                    mLoadingProgressBar.setVisibility(View.GONE);
                    mLoadingTextView.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    // using a new cursor because mCursor is not ready yet
                    c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns.MANGA_ID + "=?", new String[]{ String.valueOf(mMangaId) }, null);
                    if (c != null) {
                        try {
                            if (c.getCount() <= 0) {
                                mEmptyStateTextView.setVisibility(View.VISIBLE);
                            } else {
                                mEmptyStateTextView.setVisibility(View.GONE);
                            }
                        } finally {
                            c.close();
                        }
                    }
                    break;
                case WSIntentService.CHECK_LAST_SCAN:
                    switch (intent.getIntExtra(WSIntentService.EXTRA_RESULT_CODE, 0)) {
                        case WSIntentService.RESULT_OK:
                            String lastScan = intent.getStringExtra(WSIntentService.PARAM_LAST_SCAN);

                            c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns._ID + "=?", new String[]{ String.valueOf(mMangaId) }, null);
                            if (c != null) {
                                try {
                                    if (c.getCount() == 1) {
                                        c.moveToFirst();
                                        if (!c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.LAST_SCAN)).equals(lastScan)) {
                                            // new scan available to download, inform user
                                            ((AppCompatActivity) getActivity()).getSupportActionBar().setLogo(R.drawable.ic_fiber_new_blue_24dp);
                                            mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_new_scans_available), Snackbar.LENGTH_LONG);

                                            mSnackbar.show();
                                        }
                                    }
                                } finally {
                                    c.close();
                                }
                            }
                            break;
                        case WSIntentService.RESULT_ERROR_NO_INTERNET:
                            mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_no_internet), Snackbar.LENGTH_LONG);

                            mSnackbar.show();
                            break;
                        case WSIntentService.RESULT_ERROR_BAD_RESPONSE:
                        case WSIntentService.RESULT_ERROR_TIMEOUT:
                            mLoadingProgressBar.setVisibility(View.GONE);
                            mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_server_error), Snackbar.LENGTH_LONG);

                            mSnackbar.show();
                            break;
                        default:
                            break;
                    }

                    break;
                case WSIntentService.DOWNLOAD_PAGES_FOR_SCAN:
                    switch (intent.getIntExtra(WSIntentService.EXTRA_RESULT_CODE, 0)) {
                        case WSIntentService.RESULT_ERROR_NO_INTERNET:
                            mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_no_internet), Snackbar.LENGTH_LONG);

                            mSnackbar.show();
                            break;
                        case WSIntentService.RESULT_ERROR_ALREADY_DOWNLOADING:
                            mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_scan_download_already_in_progress), Snackbar.LENGTH_LONG);

                            mSnackbar.show();
                            break;
                        default:
                            break;
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
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_scans, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mMangaId = getArguments().getInt("mangaId", -1);
        mLoadingProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar_loading);
        mLoadingTextView = (TextView) view.findViewById(R.id.text_view_loading);
        mEmptyStateTextView = (TextView) view.findViewById(R.id.text_view_empty_state);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_scans);
        mAdapter = new ScanAdapter(getContext(), this, this);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mCursorFilter = "";
        mScansSearchView = (SearchView) view.findViewById(R.id.search_view_scans);
        mScansSearchView.setOnQueryTextListener(this);
        mScansSearchView.setOnClickListener(this);
        mSnackbarCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinator_layout_snackbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        getLoaderManager().initLoader(SCAN_LOADER, null, this);

        Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns.MANGA_ID + "=?", new String[]{ String.valueOf(mMangaId) }, null);
        if (c != null) {
            try {
                if (c.getCount() >= 1) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    mLoadingTextView.setVisibility(View.GONE);
                    mScansSearchView.setVisibility(View.VISIBLE);
                    checkLastScan();
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
                            if (isConnected()) {
                                intent = new Intent(getActivity(), PageActivity.class);
                                intent.putExtra(WSIntentService.PARAM_MANGA_SLUG, c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.SLUG)));
                                intent.putExtra(PageActivity.EXTRA_SCAN_ID, mCursor.getInt(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns._ID)));
                                intent.putExtra(PageActivity.EXTRA_ONLINE_READING, true);
                                startActivity(intent);
                            } else {
                                mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_no_internet), Snackbar.LENGTH_LONG);

                                mSnackbar.show();
                            }

//                            intent = new Intent(getActivity(), WSIntentService.class);
//                            intent.setAction(WSIntentService.DOWNLOAD_PAGES_FOR_SCAN);
//                            intent.putExtra(WSIntentService.PARAM_MANGA_SLUG, c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.SLUG)));
//                            intent.putExtra(WSIntentService.PARAM_SCAN_ID, mCursor.getInt(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns._ID)));
//                            getActivity().startService(intent);
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
                                    intent, false, true, true, R.string.dialog_resume_or_delete_download_cancel_button, R.string.dialog_resume_or_delete_download_resume_button, R.string.dialog_resume_or_delete_download_delete_button);
                            alertDialogFragment.setTargetFragment(this, REQUEST_RESUME_OR_DELETE_DOWNLOAD_DIALOG);
                            alertDialogFragment.show(getFragmentManager(), AlertDialogFragment.TAG);
                            break;
                        case DOWNLOAD_IN_PROGRESS:
                            intent.putExtra(EXTRA_SCAN_ID, mCursor.getInt(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns._ID)));
                            alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.dialog_stop_download_title), getString(R.string.dialog_stop_download_message, mCursor.getString(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns.NAME))), intent, false, true);
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
                            alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.dialog_delete_scan_title), getString(R.string.dialog_delete_scan_message, mCursor.getString(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns.NAME))), intent, false, true);
                            alertDialogFragment.setTargetFragment(this, REQUEST_DELETE_SCAN_DIALOG);
                            alertDialogFragment.show(getFragmentManager(), AlertDialogFragment.TAG);
                            break;
                        case NOT_DOWNLOADED:
                            intent = new Intent(getActivity(), WSIntentService.class);
                            intent.setAction(WSIntentService.DOWNLOAD_PAGES_FOR_SCAN);
                            intent.putExtra(WSIntentService.PARAM_MANGA_SLUG, c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.SLUG)));
                            intent.putExtra(WSIntentService.PARAM_SCAN_ID, mCursor.getInt(mCursor.getColumnIndex(ShonenTouchContract.ScanColumns._ID)));
                            getActivity().startService(intent);
                            break;
                        default:
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
                        // check that the status is still DOWNLOAD_IN_PROGRESS
                        int scanId = data.getIntExtra(EXTRA_SCAN_ID, -1);
                        Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{ String.valueOf(scanId) }, null);
                        if (c != null && c.getCount() == 1) {
                            try {
                                c.moveToFirst();
                                if (Scan.Status.valueOf(c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.STATUS))) == Scan.Status.DOWNLOAD_IN_PROGRESS) {
                                    // stop download only if download is in progress
                                    ContentValues updatedScan = new ContentValues();
                                    updatedScan.put(ShonenTouchContract.ScanColumns.STATUS, Scan.Status.DOWNLOAD_STOPPED.name());
                                    getActivity().getContentResolver().update(ShonenTouchContract.Scan.CONTENT_URI, updatedScan, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(scanId)});
                                }
                            } finally {
                                c.close();
                            }
                        }
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
                    case AlertDialogFragment.RESULT_NEGATIVE:
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
            if (mCursorFilter == null || mCursorFilter.equals("")) {
                return new CursorLoader(getContext().getApplicationContext(), ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns.MANGA_ID + "=?", new String[]{ String.valueOf(mMangaId)}, ShonenTouchContract.ScanColumns._ID + " DESC");
            } else {
                return new CursorLoader(getContext().getApplicationContext(), ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns.MANGA_ID + "=?" + " AND " + ShonenTouchContract.ScanColumns.NAME + " LIKE ?",
                        new String[]{ String.valueOf(mMangaId), "%" + mCursorFilter + "%" }, ShonenTouchContract.ScanColumns._ID + " DESC");
            }
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
        filter.addAction(WSIntentService.CHECK_LAST_SCAN);
        filter.addAction(WSIntentService.DOWNLOAD_PAGES_FOR_SCAN);

        getActivity().registerReceiver(mBroadcastReceiver, filter);

        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mCursorFilter = !TextUtils.isEmpty(newText) ? newText : null;
        getLoaderManager().restartLoader(SCAN_LOADER, null, this);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_view_scans:
                mScansSearchView.setIconified(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_favorite);

        if (item != null) {
            Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns._ID + "=?", new String[]{ String.valueOf(mMangaId) }, null);
            if (c != null && c.getCount() == 1) {
                try {
                    c.moveToFirst();
                    if (c.getInt(c.getColumnIndex(ShonenTouchContract.MangaColumns.FAVORITE)) == 1) {
                        DrawableCompat.setTint(item.getIcon(), ContextCompat.getColor(getActivity(), android.R.color.holo_orange_dark));
                    } else {
                        DrawableCompat.setTint(item.getIcon(), ContextCompat.getColor(getActivity(), android.R.color.darker_gray));
                    }
                } finally {
                    c.close();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns._ID + "=?", new String[]{ String.valueOf(mMangaId) }, null);
                if (c != null && c.getCount() == 1) {
                    try {
                        c.moveToFirst();
                        ContentValues updatedManga = new ContentValues();
                        if (c.getInt(c.getColumnIndex(ShonenTouchContract.MangaColumns.FAVORITE)) == 1) {
                            updatedManga.put(ShonenTouchContract.MangaColumns.FAVORITE, false);
                            DrawableCompat.setTint(item.getIcon(), ContextCompat.getColor(getActivity(), android.R.color.darker_gray));
                            mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.NAME)) + " retiré des favoris", Snackbar.LENGTH_LONG);
                        } else {
                            updatedManga.put(ShonenTouchContract.MangaColumns.FAVORITE, true);
                            DrawableCompat.setTint(item.getIcon(), ContextCompat.getColor(getActivity(), android.R.color.holo_orange_dark));
                            mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.NAME)) + " ajouté aux favoris", Snackbar.LENGTH_LONG);
                        }
                        getActivity().getContentResolver().update(ShonenTouchContract.Manga.CONTENT_URI, updatedManga, ShonenTouchContract.MangaColumns._ID + "=?", new String[]{String.valueOf(mMangaId)});
                        mSnackbar.show();
                    } finally {
                        c.close();
                    }
                }
                break;
            case android.R.id.home:
                getActivity().finish();
                break;
            default:
                break;
        }

        return true;
    }


    @Override
    public void onRefresh() {
        fetchScans();
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

    private void checkLastScan() {
        final Intent intent = new Intent(getActivity(), WSIntentService.class);

        intent.setAction(WSIntentService.CHECK_LAST_SCAN);
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

    /**
     * Indicates whether network connectivity exists.
     * @return true if network connectivity exists, false otherwise.
     */
    private boolean isConnected() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
