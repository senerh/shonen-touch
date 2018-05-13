package ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import io.github.senerh.shonentouch.R;
import model.database.ShonenTouchContract;
import model.database.ShonenTouchContract.PageColumns;
import model.entities.Scan;
import model.services.WSIntentService;
import ui.views.ExtendedViewPager;
import ui.views.TouchImageView;

public class PageActivity extends AppCompatActivity implements ExtendedViewPager.OnSwipeOutListener {
    // save instance state
    private static final String EXTRA_BITMAPS_LIST = "EXTRA_BITMAPS_LIST";
    private static final String EXTRA_IMAGES_COUNT = "EXTRA_IMAGES_COUNT";
    private static final String EXTRA_IMAGES_URLS_LIST = "EXTRA_IMAGES_URLS_LIST";

    private static final long MAX_TIME_TO_TRIGGER_NEXT = 2000;

    public static final String EXTRA_SCAN_ID = "EXTRA_SCAN_ID";
    public static final String EXTRA_ONLINE_READING = "EXTRA_ONLINE_READING";

    private ProgressBar mProgressBar;
    private CoordinatorLayout mSnackbarCoordinatorLayout;
    private Snackbar mSnackbar;
    // list used in the thread, as member for save instance state
    private List<String> mImagesUrlsList;
    private List<Bitmap> mImagesList;
    private int mImagesCount;
    private int mScanId;
    private boolean mThreadInterrupted;
    private long mLastSwipeEndTimestamp;
//    private ImagePagerAdapter mImagePagerAdapter;
//    private ProgressDialogFragment progressDialogFragment;

    private ProgressDialog progressDialog;

    private ExtendedViewPager mExtendedViewPager;
//    private Thread mThread;

//    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Cursor c;
//            switch (intent.getAction()) {
//                case WSIntentService.GET_PAGES_URLS_FOR_SCAN:
//                    switch (intent.getIntExtra(WSIntentService.EXTRA_RESULT_CODE, 0)) {
//                        case WSIntentService.RESULT_OK:
//                            mImagesUrlsList = intent.getStringArrayListExtra(WSIntentService.PARAM_PAGES_URLS);
//                            Intent requestIntent = new Intent(getApplicationContext(), WSIntentService.class);
//
//                            requestIntent.setAction(WSIntentService.GET_BITMAP_PAGE);
//                            if (mImagesList.isEmpty()) {
////                                requestIntent.putExtra(WSIntentService.PARAM_PAGE_URL, mImagesUrlsList.get(0));
//                                new DownloadImageTask().execute(mImagesUrlsList.get(0));
//                            } else {
////                                requestIntent.putExtra(WSIntentService.PARAM_PAGE_URL, mImagesUrlsList.get(mImagesList.size() - 1));
//                                new DownloadImageTask().execute(mImagesUrlsList.get(mImagesList.size() - 1));
//                            }
//
//                            mProgressBar.setProgress(100 / mImagesUrlsList.size());
//                            mImagesCount = mImagesUrlsList.size();
//                            startService(requestIntent);
//                            break;
//                        case WSIntentService.RESULT_ERROR_NO_INTERNET:
//                            break;
//                        case WSIntentService.RESULT_ERROR_BAD_RESPONSE:
//                        case WSIntentService.RESULT_ERROR_TIMEOUT:
//                            break;
//                        default:
//                            break;
//                    }
//                    break;
//                case WSIntentService.GET_BITMAP_PAGE:
//                    switch (intent.getIntExtra(WSIntentService.EXTRA_RESULT_CODE, 0)) {
//                        case WSIntentService.RESULT_OK:
//
////                            mImagesList.add((Bitmap) intent.getParcelableExtra(WSIntentService.PARAM_BITMAP));
////                            mExtendedViewPager.getAdapter().notifyDataSetChanged();
//                            String url = intent.getStringExtra(WSIntentService.PARAM_PAGE_URL);
//                            // look for the next url to call
//                            for (int i = 0; i < mImagesUrlsList.size(); i++) {
//                                if (mImagesUrlsList.get(i).equals(url)) {
//                                    if (i < (mImagesUrlsList.size() - 1)) {
//                                        // launch download of next image
//                                        new DownloadImageTask().execute(mImagesUrlsList.get(i + 1));
//                                        mCurrentPageTextView.setText("Page " + i + " sur " + mImagesUrlsList.size());
////                                        Intent requestIntent = new Intent(getApplicationContext(), WSIntentService.class);
////
////                                        requestIntent.setAction(WSIntentService.GET_BITMAP_PAGE);
////                                        requestIntent.putExtra(WSIntentService.PARAM_PAGE_URL, mImagesUrlsList.get(i + 1));
////                                        startService(requestIntent);
//                                    } else {
//                                        mCurrentPageTextView.setVisibility(View.GONE);
//                                    }
//                                }
//                            }
//                            break;
//                        case WSIntentService.RESULT_ERROR_NO_INTERNET:
//                            break;
//                        case WSIntentService.RESULT_ERROR_BAD_RESPONSE:
//                        case WSIntentService.RESULT_ERROR_TIMEOUT:
//                            break;
//                        default:
//                            break;
//                    }
//                    break;
//                default :
//                    break;
//            }
//        }
//    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        setImmersiveScreen();
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mSnackbarCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout_snackbar);
        mExtendedViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
        mExtendedViewPager.setOnSwipeOutListener(this);

        mExtendedViewPager.addOnPageChangeListener(new DetailOnPageChangeListener());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mImagesList = new ArrayList<>();
        mScanId = getIntent().getIntExtra(EXTRA_SCAN_ID, -1);
        if (mScanId != -1) {
            if (getIntent().getBooleanExtra(EXTRA_ONLINE_READING, false)) {
                // online reading
                if (savedInstanceState != null) {
                    System.out.println("******sauvegarde : ");
                    mImagesCount = savedInstanceState.getInt(EXTRA_IMAGES_COUNT);
                    List<Bitmap> bitmaps = savedInstanceState.getParcelableArrayList(EXTRA_BITMAPS_LIST);
                    if (bitmaps != null) {
                        System.out.println("************le add du save instance");
                        mImagesList = bitmaps;
                        mExtendedViewPager.setAdapter(new ImagePagerAdapter());

                    }
//                    List<String> imagesUrls = savedInstanceState.getStringArrayList(EXTRA_IMAGES_URLS_LIST);
//                    if (imagesUrls != null) {
//                        mImagesUrlsList = imagesUrls;
//
//                    }
                } else {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Lecture en ligne (long clic sur le scan pour télécharger)");
                    progressDialog.setMessage("Initialisation");
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    });
                    progressDialog.show();
                    new DownloadImageTask(getMangaSlug(), getScanName()).execute();
                }
            } else {
                // read downloaded pages, offline reading
                Cursor c = getContentResolver().query(ShonenTouchContract.Page.CONTENT_URI, null, PageColumns.SCAN_ID + "=?", new String[]{String.valueOf(mScanId)}, null);
                if (c != null) {
                    try {
                        c.moveToFirst();
                        while (!c.isAfterLast()) {
                            Options options = new Options();
                            options.inPreferredConfig = Config.ARGB_8888;
                            mImagesList.add(BitmapFactory.decodeFile(c.getString(c.getColumnIndex(PageColumns.PATH)), options));
                            c.moveToNext();
                        }
                        mProgressBar.setProgress(100 / mImagesList.size());
                        mImagesCount = mImagesList.size();
                        mExtendedViewPager.setAdapter(new ImagePagerAdapter());
                    } finally {
                        c.close();
                    }
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(EXTRA_BITMAPS_LIST, (ArrayList<? extends Parcelable>) mImagesList);
        outState.putInt(EXTRA_IMAGES_COUNT, mImagesCount);
        // also put the pages urls to avoid doing the request twice
        outState.putStringArrayList(EXTRA_IMAGES_URLS_LIST, (ArrayList<String>) mImagesUrlsList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
//        IntentFilter filter = new IntentFilter();
//
//        filter.addAction(WSIntentService.GET_PAGES_URLS_FOR_SCAN);
//        filter.addAction(WSIntentService.GET_BITMAP_PAGE);
//
//        registerReceiver(mBroadcastReceiver, filter);
        setImmersiveScreen();
    }

//    @Override
//    public void onPause() {
//        unregisterReceiver(mBroadcastReceiver);
//        super.onPause();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setImmersiveScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
        decorView.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
    }

    @Override
    public void onSwipeOutAtStart() {
        Cursor c = getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(mScanId)}, null);
        if (c != null) {
            try {
                if (c.getCount() == 1) {
                    c.moveToFirst();
                    String scanName = c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.NAME));
                    int mangaId = c.getInt(c.getColumnIndex(ShonenTouchContract.ScanColumns.MANGA_ID));
                    Cursor mangaCursor = getApplicationContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns._ID + "=?", new String[]{ String.valueOf(mangaId) }, null);
                    if (mangaCursor != null && mangaCursor.getCount() == 1) {
                        try {
                            mangaCursor.moveToFirst();
                            String mangaName = mangaCursor.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.NAME));
                            mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_swipe_start_page, scanName, mangaName), Snackbar.LENGTH_LONG);

                            mSnackbar.show();
                        } finally {
                            mangaCursor.close();
                        }
                    }
                }
            } finally {
                c.close();
            }
        }
    }

    @Override
    public void onSwipeOutAtEnd() {
        if (mImagesCount == mImagesList.size()) {
            // first check if there is a next scan
            if (isLastScan()) {
                mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_last_scan), Snackbar.LENGTH_LONG);

                mSnackbar.show();
            } else {
                long eventTimestamp = Calendar.getInstance().getTime().getTime();
                if ((eventTimestamp - mLastSwipeEndTimestamp) > MAX_TIME_TO_TRIGGER_NEXT) {
                    mLastSwipeEndTimestamp = eventTimestamp;
                    mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_swipe_again_to_load_next), Snackbar.LENGTH_LONG);

                    mSnackbar.show();
                } else {
                    Intent intent = new Intent(this, PageActivity.class);
                    // load next scan
                    if (!isNextScanAlreadyDownloaded()) {
                        intent.putExtra(WSIntentService.PARAM_MANGA_SLUG, getMangaSlug());
                        intent.putExtra(PageActivity.EXTRA_ONLINE_READING, true);
                    }
                    intent.putExtra(PageActivity.EXTRA_SCAN_ID, mScanId + 1);
                    finish();

                    startActivity(intent);
                }
            }
        } else {
            mSnackbar = Snackbar.make(mSnackbarCoordinatorLayout, getResources().getString(R.string.snackbar_next_page_not_ready_yet), Snackbar.LENGTH_LONG);

            mSnackbar.show();
        }
    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Void> {
        String mangaSlug, scanName;
        List<String> urls;

        public DownloadImageTask(String mangaSlug, String scanName) {
            this.mangaSlug = mangaSlug;
            this.scanName = scanName;
        }

        @Override
        protected void onPreExecute() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

            super.onPreExecute();
        }

        protected Void doInBackground(Void... voids) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(String.format(WSIntentService.URL_ALL_PAGES_FOR_MANGA_AND_SCAN, mangaSlug, scanName)).openConnection();
                Scanner s = new Scanner(new BufferedInputStream(urlConnection.getInputStream())).useDelimiter("\\A");
                String result = s.hasNext() ? s.next() : "";
                urls = new ArrayList<>();

                JSONArray pagesJSONArray = new JSONArray(result);

                for (int i = 0; i < pagesJSONArray.length(); i++) {
                    urls.add(pagesJSONArray.getJSONObject(i).getString("url"));
                }
                mImagesCount = urls.size();
                urlConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < urls.size(); i++) {
                try {
                    final int p = i;
                    mImagesList.add(BitmapFactory.decodeStream(new URL(urls.get(i)).openStream()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setMessage("Page " + p + " sur " + urls.size());
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            progressDialog.dismiss();
            mProgressBar.setProgress(100 / mImagesList.size());
            mExtendedViewPager.setAdapter(new ImagePagerAdapter());
        }
    }

    private String getMangaSlug() {
        try (Cursor c = getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(mScanId), }, null)) {
            if (c != null && c.getCount() == 1) {
                c.moveToFirst();
                int mangaId = c.getInt(c.getColumnIndex(ShonenTouchContract.ScanColumns.MANGA_ID));

                try (Cursor c2 = getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns._ID + "=?", new String[]{ String.valueOf(mangaId) }, null)) {
                    if (c2 != null && c2.getCount() == 1) {
                        c2.moveToFirst();
                        return c2.getString(c2.getColumnIndex(ShonenTouchContract.MangaColumns.SLUG));
                    }
                }
            }
        }

        return "";
    }

    private String getScanName() {
        try (Cursor c = getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(mScanId), }, null)) {
            if (c != null && c.getCount() == 1) {
                c.moveToFirst();
                return c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.NAME));
            }
        }

        return "";
    }

    private boolean isLastScan() {
        // read manga id
        try (Cursor c = getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(mScanId), }, null)) {
            if (c != null && c.getCount() == 1) {
                c.moveToFirst();
                int mangaId = c.getInt(c.getColumnIndex(ShonenTouchContract.ScanColumns.MANGA_ID));

                try (Cursor c2 = getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns._ID + "=?" + " AND " + ShonenTouchContract.ScanColumns.MANGA_ID + "=?", new String[]{ String.valueOf(mScanId + 1), String.valueOf(mangaId) }, null)) {
                    if (c2 != null && c2.getCount() == 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isNextScanAlreadyDownloaded() {
        try (Cursor c = getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(mScanId + 1), }, null)) {
            if (c != null && c.getCount() == 1) {
                c.moveToFirst();
                if (Scan.Status.valueOf(c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.STATUS))) == Scan.Status.DOWNLOAD_COMPLETE) {
                    return true;
                }
            }
        }

        return false;
    }

    class ImagePagerAdapter extends PagerAdapter {
        TouchImageView mPhotoView;

        ImagePagerAdapter() {
        }

        public int getCount() {
            return mImagesList != null ? mImagesList.size() : 0;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public View instantiateItem(ViewGroup container, int position) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mPhotoView = new TouchImageView(container.getContext(), true);
                mPhotoView.setScaleType(ScaleType.CENTER_CROP);
            } else {
                mPhotoView = new TouchImageView(container.getContext(), false);
            }

            Options options = new Options();
            options.inPreferredConfig = Config.ARGB_8888;
            mPhotoView.setImageBitmap(mImagesList.get(position));
            container.addView(mPhotoView, -1, -1);

            return mPhotoView;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * Used to have the current element position to display progress bar
     */
    public class DetailOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            mProgressBar.setProgress(((position + 1) * 100) / mImagesCount);
            if (position == mImagesCount) {
                mProgressBar.setProgress(100);
            }
        }
    }

//    public List<Bitmap> cloneBitmapList(List<Bitmap> toClone) {
//        List<Bitmap> result = new ArrayList<>();
//
//        for (Bitmap b : toClone) {
//            result.add(b.copy(b.getConfig(), true));
//        }
//
//        return result;
//    }
}
