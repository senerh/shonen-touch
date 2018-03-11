package ui.fragments;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ui.activities.MangaActivity;
import io.github.senerh.shonentouch.R;
import model.adapters.MangaAdapter;
import model.adapters.OnItemClickListener;
import model.database.ShonenTouchContract;
import model.entities.Manga;
import model.services.WSIntentService;

public class MangaListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,OnItemClickListener, SearchView.OnQueryTextListener {
    // Loaders
    private static final int MANGA_LOADER = 1;
    private Cursor mCursor;

    private RecyclerView mRecyclerView;
    private MangaAdapter mAdapter;
    private String mCursorFilter;
    private SearchView mMangaSearchView;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case WSIntentService.GET_ALL_MANGA:
                    List<Manga> mangas = intent.getParcelableArrayListExtra(WSIntentService.PARAM_MANGAS_LIST);

                    // persist all new mangas in db
                    for (Manga manga : mangas) {
                        boolean alreadyExists = false;

                        for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                            if (manga.getName().equals(mCursor.getString(mCursor.getColumnIndex(ShonenTouchContract.MangaColumns.NAME)))) {
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

                            // persisting manga
                            getActivity().getApplicationContext().getContentResolver().insert(ShonenTouchContract.Manga.CONTENT_URI, newManga);
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
        return inflater.inflate(R.layout.fragment_manga_list, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        getActivity().getApplicationContext().deleteDatabase("Manga.db");

        mCursorFilter = "";
        mMangaSearchView = (SearchView) view.findViewById(R.id.search_view_manga);
        mMangaSearchView.setOnQueryTextListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_manga);
        mAdapter = new MangaAdapter(getContext(), this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(MANGA_LOADER, null, this);

        Cursor c = getActivity().getApplicationContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, null, null, null);
        if (c != null) {
            try {
                if (c.getCount() == 0) {
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
//        Uri baseUri;
//        if (mCursorFilter != null) {
//            baseUri = Uri.withAppendedPath(ShonenTouchContract.Manga.CONTENT_URI,
//                    Uri.encode(mCursorFilter));
//        } else {
//            baseUri = ShonenTouchContract.Manga.CONTENT_URI;
//        }
//
//        String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
//                + Contacts.HAS_PHONE_NUMBER + "=1) AND ("
//                + Contacts.DISPLAY_NAME + " != '' ))";
//
//        String[] projection = new String[] {
//                Contacts._ID,
//                Contacts.DISPLAY_NAME,
//                Contacts.CONTACT_STATUS,
//                Contacts.CONTACT_PRESENCE,
//                Contacts.PHOTO_ID,
//                Contacts.LOOKUP_KEY,
//        };
//
//        CursorLoader cursorLoader = new CursorLoader(
//                MainActivity.this,
//                baseUri,
//                projection,
//                select,
//                null,
//                Contacts.DISPLAY_NAME);
        if (id == MANGA_LOADER) {
            if (mCursorFilter == null || mCursorFilter.equals("")) {
                return new CursorLoader(getContext().getApplicationContext(), ShonenTouchContract.Manga.CONTENT_URI, null, null, null, null);
            } else {
                return new CursorLoader(getContext().getApplicationContext(), ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns.NAME + " LIKE ?", new String[]{ "%" + mCursorFilter + "%" }, null);
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
        // TODO Auto-generated method stub
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

    private void fetchMangas() {
        final Intent intent = new Intent(getActivity(), WSIntentService.class);

        intent.setAction(WSIntentService.GET_ALL_MANGA);
        getActivity().startService(intent);
    }
}
