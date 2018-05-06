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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class ScansFragment extends Fragment implements OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>, OnItemLongClickListener, SearchView.OnQueryTextListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    // Loaders
    private static final int SCAN_LOADER = 1;
    private Cursor mCursor;

    private static final int REQUEST_DELETE_SCAN_DIALOG = 0;
    private static final int REQUEST_STOP_DOWNLOAD_DIALOG = 1;
    private static final int REQUEST_RESUME_OR_DELETE_DOWNLOAD_DIALOG = 2;

    public static final String EXTRA_SCAN_ID = "EXTRA_SCAN_ID";
    public static final String EXTRA_MANGA_ID = "EXTRA_MANGA_ID";

    private ProgressBar mEmptyStateProgressBar;
    private TextView mEmptyStateTextView, mNewScanTextView;
    private RecyclerView mRecyclerView;
    private ScanAdapter mAdapter;
    private SearchView mScansSearchView;
    private int mMangaId;
    private String mCursorFilter;
    private CoordinatorLayout mSnackbarCoordinatorLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Snackbar snackbar;
            switch (intent.getAction()) {
                case WSIntentService.GET_ALL_SCANS_FOR_MANGA:
                    switch (intent.getIntExtra(WSIntentService.EXTRA_RESULT_CODE, 0)) {
                        case WSIntentService.RESULT_OK:
                            Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns.MANGA_ID + "=?", new String[]{ String.valueOf(mMangaId) }, null);
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

                                    mEmptyStateProgressBar.setVisibility(View.GONE);
                                    mEmptyStateTextView.setVisibility(View.GONE);
                                    mScansSearchView.setVisibility(View.VISIBLE);
                                    mNewScanTextView.setVisibility(View.GONE);
                                    ((AppCompatActivity) getActivity()).getSupportActionBar().setLogo(null);
                                    if (mSwipeRefreshLayout.isRefreshing()) {
                                        snackbar = Snackbar.make(mSnackbarCoordinatorLayout, "Liste de scans récupérée", Snackbar.LENGTH_LONG);

                                        snackbar.show();
                                    }
                                } finally {
                                    c.close();
                                }
                            }
                            break;
                        case WSIntentService.RESULT_ERROR_NO_INTERNET:
                            mEmptyStateProgressBar.setVisibility(View.GONE);
                            snackbar = Snackbar.make(mSnackbarCoordinatorLayout, "Aucune connexion internet", Snackbar.LENGTH_LONG);

                            snackbar.show();
                            break;
                        case WSIntentService.RESULT_ERROR_BAD_RESPONSE:
                        case WSIntentService.RESULT_ERROR_TIMEOUT:
                            mEmptyStateProgressBar.setVisibility(View.GONE);
                            snackbar = Snackbar.make(mSnackbarCoordinatorLayout, "Erreur de communication avec le serveur", Snackbar.LENGTH_LONG);

                            snackbar.show();
                            break;
                        default:
                            break;
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case WSIntentService.CHECK_LAST_SCAN:
                    String lastScan = intent.getStringExtra(WSIntentService.PARAM_LAST_SCAN);

                    Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns._ID + "=?", new String[]{ String.valueOf(mMangaId) }, null);
                    if (c != null) {
                        try {
                            if (c.getCount() == 1) {
                                c.moveToFirst();
                                if (!c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.LAST_SCAN)).equals(lastScan)) {
                                    // new scan available to download, inform user
                                    mNewScanTextView.setVisibility(View.VISIBLE);
                                    ((AppCompatActivity) getActivity()).getSupportActionBar().setLogo(R.drawable.ic_fiber_new_blue_24dp);
                                }
                            }
                        } finally {
                            c.close();
                        }
                    }
                    break;
                case WSIntentService.DOWNLOAD_PAGES_FOR_SCAN:
                    switch (intent.getIntExtra(WSIntentService.EXTRA_RESULT_CODE, 0)) {
                        case WSIntentService.RESULT_ERROR_NO_INTERNET:
                            snackbar = Snackbar.make(mSnackbarCoordinatorLayout, "Aucune connexion internet", Snackbar.LENGTH_LONG);

                            snackbar.show();
                            break;
                        case WSIntentService.RESULT_ERROR_ALREADY_DOWNLOADING:
                            snackbar = Snackbar.make(mSnackbarCoordinatorLayout, "Téléchargement d'un scan déjà en cours", Snackbar.LENGTH_LONG);

                            snackbar.show();
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
        return inflater.inflate(R.layout.fragment_scans, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mMangaId = getArguments().getInt("mangaId", -1);
        mEmptyStateProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar_empty_state);
        mEmptyStateTextView = (TextView) view.findViewById(R.id.text_view_first_time);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_scans);
        mAdapter = new ScanAdapter(getContext(), this, this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mCursorFilter = "";
        mScansSearchView = (SearchView) view.findViewById(R.id.search_view_scans);
        mScansSearchView.setOnQueryTextListener(this);
        mScansSearchView.setOnClickListener(this);
        mNewScanTextView = (TextView) view.findViewById(R.id.text_view_new_scan);
        mSnackbarCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinator_layout_snackbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        getLoaderManager().initLoader(SCAN_LOADER, null, this);

        Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns.MANGA_ID + "=?", new String[]{ String.valueOf(mMangaId) }, null);
        if (c != null) {
            try {
                if (c.getCount() >= 1) {
                    mEmptyStateProgressBar.setVisibility(View.GONE);
                    mEmptyStateTextView.setVisibility(View.GONE);
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
                        int scanId = data.getIntExtra(EXTRA_SCAN_ID, -1);
                        ContentValues updatedScan = new ContentValues();
                        updatedScan.put(ShonenTouchContract.ScanColumns.STATUS, Scan.Status.DOWNLOAD_STOPPED.name());
                        getActivity().getContentResolver().update(ShonenTouchContract.Scan.CONTENT_URI, updatedScan, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(scanId)});
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
}
