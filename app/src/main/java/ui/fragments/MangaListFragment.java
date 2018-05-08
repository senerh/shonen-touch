package ui.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import model.adapters.OnItemClickListener;
import ui.activities.HelpActivity;
import ui.activities.MangaActivity;
import io.github.senerh.shonentouch.R;
import model.adapters.MangaAdapter;
import model.database.ShonenTouchContract;
import model.entities.Manga;
import model.services.WSIntentService;

public class MangaListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener, SearchView.OnQueryTextListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    // Loaders
    private static final int MANGA_LOADER = 1;
    private Cursor mCursor;
    private Account mAccount;

    private static final int SYNC_FREQUENCY_SECONDS = 24 * 60 * 60;

    private MangaAdapter mAdapter;
    private String mCursorFilter;
    private SearchView mMangaSearchView;
    private ProgressBar mLoadingProgressBar;
    private TextView mLoadingTextView, mEmptyStateTextView;
    private CoordinatorLayout mSnackbarCoordinatorLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Snackbar snackbar;
            switch (intent.getAction()) {
                case WSIntentService.GET_ALL_MANGA:
                    switch (intent.getIntExtra(WSIntentService.EXTRA_RESULT_CODE, 0)) {
                        case WSIntentService.RESULT_OK:
                            Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, null, null, null);
                            if (c != null) {
                                try {
                                    List<Manga> mangas = intent.getParcelableArrayListExtra(WSIntentService.PARAM_MANGAS_LIST);

                                    // persist all new mangas in db
                                    for (Manga manga : mangas) {
                                        boolean alreadyExists = false;

                                        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                                            if (manga.getName().equals(c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.NAME)))) {
                                                alreadyExists = true;
                                            }
                                        }

                                        // persist
                                        if (!alreadyExists) {
                                            ContentValues newManga = new ContentValues();

                                            newManga.put(ShonenTouchContract.MangaColumns.NAME, manga.getName());
                                            newManga.put(ShonenTouchContract.MangaColumns.SLUG, manga.getSlug());
                                            newManga.put(ShonenTouchContract.MangaColumns.LAST_SCAN, manga.getLastScan());
                                            newManga.put(ShonenTouchContract.MangaColumns.ICON_PATH, manga.getIconPath());
                                            newManga.put(ShonenTouchContract.MangaColumns.FAVORITE, false);

                                            // persisting manga
                                            getActivity().getApplicationContext().getContentResolver().insert(ShonenTouchContract.Manga.CONTENT_URI, newManga);
                                        }
                                    }
                                    mMangaSearchView.setVisibility(View.VISIBLE);
                                    if (mSwipeRefreshLayout.isRefreshing()) {
                                        snackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_mangas_received), Snackbar.LENGTH_LONG);

                                        snackbar.show();
                                    }
                                } finally {
                                    c.close();
                                }
                            }
                            break;
                        case WSIntentService.RESULT_ERROR_NO_INTERNET:
                            snackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_no_internet), Snackbar.LENGTH_LONG);

                            snackbar.show();
                            break;
                        case WSIntentService.RESULT_ERROR_BAD_RESPONSE:
                        case WSIntentService.RESULT_ERROR_TIMEOUT:
                            snackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_server_error), Snackbar.LENGTH_LONG);

                            snackbar.show();
                            break;
                        default:
                            break;
                    }
                    mLoadingProgressBar.setVisibility(View.GONE);
                    mLoadingTextView.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    // using a new cursor because mCursor is not ready yet
                    Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, null, null, null);
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
                default :
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_manga_list, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        getActivity().getApplicationContext().deleteDatabase("Manga.db");

        mLoadingProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar_loading);
        mLoadingTextView = (TextView) view.findViewById(R.id.text_view_loading);
        mEmptyStateTextView = (TextView) view.findViewById(R.id.text_view_empty_state);
        mCursorFilter = "";
        mMangaSearchView = (SearchView) view.findViewById(R.id.search_view_manga);
        mMangaSearchView.setOnQueryTextListener(this);
        mMangaSearchView.setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_manga);
        mAdapter = new MangaAdapter(getContext(), this);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mSnackbarCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinator_layout_snackbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        Button ronaldo = (Button) view.findViewById(R.id.meurguez);
        ronaldo.setOnClickListener(this);

        getLoaderManager().initLoader(MANGA_LOADER, null, this);

        setupPeriodicSync();

        Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, null, null, null);
        if (c != null) {
            try {
                if (c.getCount() >= 1) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    mLoadingTextView.setVisibility(View.GONE);
                    mMangaSearchView.setVisibility(View.VISIBLE);
                } else if (c.getCount() == 0) {
                    fetchMangas();
                }
            } finally {
                c.close();
            }
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == MANGA_LOADER) {
            if (mCursorFilter == null || mCursorFilter.equals("")) {
                return new CursorLoader(getContext().getApplicationContext(), ShonenTouchContract.Manga.CONTENT_URI, null, null, null, ShonenTouchContract.MangaColumns.FAVORITE + " DESC");
            } else {
                return new CursorLoader(getContext().getApplicationContext(), ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns.NAME + " LIKE ?", new String[]{ "%" + mCursorFilter + "%" }, ShonenTouchContract.MangaColumns.FAVORITE + " DESC");
            }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == MANGA_LOADER) {
            mCursor = data;
            mAdapter.swapCursor(mCursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == MANGA_LOADER) {
            mCursor = null;
            mAdapter.swapCursor(mCursor);
        }
    }

    @Override
    public boolean onQueryTextChange(String arg0) {
        mCursorFilter = !TextUtils.isEmpty(arg0) ? arg0 : null;
        getLoaderManager().restartLoader(MANGA_LOADER, null, this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String arg0) {
        return false;
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

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), MangaActivity.class);

        mCursor.moveToPosition(position);
        int mangaId = mCursor.getInt(mCursor.getColumnIndex(ShonenTouchContract.MangaColumns._ID));
        String mangaName = mCursor.getString(mCursor.getColumnIndex(ShonenTouchContract.MangaColumns.NAME));

        intent.putExtra("mangaId", mangaId);
        intent.putExtra("mangaName", mangaName);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_view_manga:
                mMangaSearchView.setIconified(false);
                break;
            case R.id.meurguez:
                Account[] accs = AccountManager.get(getContext()).getAccountsByType(getContext().getString(R.string.account_type));
                Account account;
                if (accs.length > 0) {
                    account = accs[0];
                } else {
                    account = new Account(getContext().getString(R.string.account_name), getContext().getString(R.string.account_type));
                    boolean accountCreated = AccountManager.get(getContext()).addAccountExplicitly(account, "", null);
                }
                ContentResolver.requestSync(account, getContext().getString(R.string.authorities), new Bundle());
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        fetchMangas();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                Intent intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }

    private void fetchMangas() {
        final Intent intent = new Intent(getActivity(), WSIntentService.class);

        intent.setAction(WSIntentService.GET_ALL_MANGA);
        getActivity().startService(intent);
    }

    private void createOrFindAccount() {
        Account[] accounts = AccountManager.get(getActivity()).getAccountsByType(getString(R.string.account_type));
        if (accounts.length > 0) {
            mAccount = accounts[0];
        } else {
            mAccount = new Account("Sync Shonen Touch", getString(R.string.account_type));
            boolean accountCreated = AccountManager.get(getActivity()).addAccountExplicitly(mAccount, "password", null);
        }
    }

    private void setupPeriodicSync() {
        createOrFindAccount();
        ContentResolver.setIsSyncable(mAccount, getString(R.string.authorities), 1);
        ContentResolver.setSyncAutomatically(mAccount, getString(R.string.authorities), true);
        // 86400 seconds for a one day period
        ContentResolver.addPeriodicSync(mAccount, getString(R.string.authorities), new Bundle(), SYNC_FREQUENCY_SECONDS);
    }
}
