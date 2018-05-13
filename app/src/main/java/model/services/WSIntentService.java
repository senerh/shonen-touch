package model.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Base64;

import org.apache.commons.cli.HelpFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.database.ShonenTouchContract;
import model.entities.Manga;
import model.entities.Page;
import model.entities.Scan;

/**
 * Created by Thibaut SORIANO on 28/03/2017.
 */
public class WSIntentService extends IntentService {
    public static final String GET_ALL_MANGA = "GET_ALL_MANGA";
    public static final String GET_ALL_SCANS_FOR_MANGA = "GET_ALL_SCANS_FOR_MANGA";
    public static final String DOWNLOAD_PAGES_FOR_SCAN = "DOWNLOAD_PAGES_FOR_SCAN";
    public static final String CHECK_LAST_SCAN = "CHECK_LAST_SCAN";
    public static final String GET_BITMAPS_PAGES_FOR_SCAN = "GET_BITMAPS_PAGES_FOR_SCAN";
    public static final String GET_PAGES_URLS_FOR_SCAN = "GET_PAGES_URLS_FOR_SCAN";
    public static final String GET_BITMAP_PAGE = "GET_BITMAP_PAGE";

    private static final String URL_SERVER = "http://senerh.xyz:8080/shonen-touch-api-3/";
    public static final String URL_ALL_MANGA = URL_SERVER + "mangas";
    private static final String URL_ALL_SCANS_FOR_MANGA = URL_SERVER + "mangas/%1$s/scans";
    public static final String URL_ALL_PAGES_FOR_MANGA_AND_SCAN = URL_SERVER + "mangas/%1$s/scans/%2$s/pages";
    private static final String URL_SPECIFIC_MANGA = URL_SERVER + "mangas/%1$s";

    private static final String IMAGES_FOLDER_NAME = "shonentouch";

    public static final String PARAM_MANGAS_LIST = "mangasList";
    public static final String PARAM_MANGA_SLUG = "mangaSlug";
    public static final String PARAM_SCAN_NAME = "scanName";
    public static final String PARAM_SCANS_LIST = "scansList";
    public static final String PARAM_SCAN_ID = "scanId";
    public static final String PARAM_LAST_SCAN = "lastScan";
    public static final String PARAM_BITMAP = "bitmap";
    public static final String PARAM_PAGE_URL = "pageUrl";
    public static final String PARAM_PAGES_URLS = "pagesUrls";

    public static final String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";

    public static final int RESULT_OK = 200;
    public static final int RESULT_ERROR_NO_INTERNET = 400;
    public static final int RESULT_ERROR_ALREADY_DOWNLOADING = 444;
    public static final int RESULT_ERROR_TIMEOUT = 404;
    public static final int RESULT_ERROR_BAD_RESPONSE = 500;

    private static Thread mDownloadThread;

    public WSIntentService() {
        super("WSIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        switch (intent.getAction()) {
            case GET_ALL_MANGA:
                getAllManga();
                break;
            case GET_ALL_SCANS_FOR_MANGA:
                if (!"".equals(intent.getStringExtra(PARAM_MANGA_SLUG))) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getAllScansForManga(intent.getStringExtra(PARAM_MANGA_SLUG));
                        }
                    }).start();
                }
                break;
            case DOWNLOAD_PAGES_FOR_SCAN:
                if (!"".equals(intent.getStringExtra(PARAM_MANGA_SLUG)) && intent.getIntExtra(PARAM_SCAN_ID, -1) != -1) {
                    Intent broadcastIntent = new Intent(DOWNLOAD_PAGES_FOR_SCAN);

                    if (!isConnected()) {
                        broadcastIntent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_NO_INTERNET);
                        sendBroadcast(broadcastIntent);
                        return;
                    }

                    if (mDownloadThread == null || !mDownloadThread.isAlive()) {
                        mDownloadThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                downloadPagesImagesForScan(intent.getStringExtra(PARAM_MANGA_SLUG), intent.getIntExtra(PARAM_SCAN_ID, -1));
                            }
                        });
                        mDownloadThread.start();
                    } else {
                        broadcastIntent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_ALREADY_DOWNLOADING);
                        intent.putExtra(WSIntentService.PARAM_SCAN_ID, intent.getIntExtra(PARAM_SCAN_ID, -1));
                        sendBroadcast(broadcastIntent);
                    }
                }
                break;
            case CHECK_LAST_SCAN:
                if (!"".equals(intent.getStringExtra(PARAM_MANGA_SLUG))) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            checkLastScan(intent.getStringExtra(PARAM_MANGA_SLUG));
                        }
                    }).start();
                }
                break;
//            case GET_BITMAP_PAGE:
//                if (!"".equals(intent.getStringExtra(PARAM_PAGE_URL))) {
//                    getBitmapPage(intent.getStringExtra(PARAM_PAGE_URL));
//                }
//                break;
            case GET_PAGES_URLS_FOR_SCAN:
                if (!"".equals(intent.getStringExtra(PARAM_MANGA_SLUG)) && !"".equals(intent.getStringExtra(PARAM_SCAN_NAME))) {
                    getPagesUrlsForScan(intent.getStringExtra(PARAM_MANGA_SLUG), intent.getStringExtra(PARAM_SCAN_NAME));
                }
                break;
            default:
                break;
        }
    }

    public void getAllManga() {
        final Intent intent = new Intent(GET_ALL_MANGA);

        if (!isConnected()) {
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_NO_INTERNET);
            sendBroadcast(intent);
            return;
        }

        try {
            URL url = new URL(URL_ALL_MANGA);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";

            List<Manga> mangas = new ArrayList<>();
            JSONArray tabManga = new JSONArray(result);

            for (int i = 0; i < tabManga.length(); i++) {
                JSONObject currentManga = tabManga.getJSONObject(i);
                mangas.add(new Manga(currentManga.getString("name"), currentManga.getString("slug"), currentManga.getString("lastScan"), currentManga.getString("url"), false));
            }

            // download icons for the mangas
            for (int i = 0; i < mangas.size(); i++) {
                // optimization : verifying if current manga exists in db
                Cursor c = getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns.SLUG + "=?", new String[]{ mangas.get(i).getSlug() }, null);
                if (c != null && c.getCount() == 0) {
                    try {
                        try {
                            Bitmap downloadedBitmap = BitmapFactory.decodeStream(new URL(mangas.get(i).getIconPath()).openStream());
                            if (downloadedBitmap != null) {
                                File imageFile = new File(new ContextWrapper(this).getDir(IMAGES_FOLDER_NAME, 0), mangas.get(i).getSlug() + HelpFormatter.DEFAULT_OPT_PREFIX + "icon");
                                OutputStream fileOutputStream = new FileOutputStream(imageFile);
                                downloadedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                fileOutputStream.close();
                                mangas.get(i).setIconPath(imageFile.getAbsolutePath());
                            }
                        } catch (FileNotFoundException e) {
                            mangas.get(i).setIconPath("");
                        }
                    } finally {
                        c.close();
                    }
                }
            }

            intent.putParcelableArrayListExtra(PARAM_MANGAS_LIST, (ArrayList<? extends Parcelable>) mangas);
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_OK);
            sendBroadcast(intent);

            urlConnection.disconnect();
        } catch (IOException e) {
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_TIMEOUT);
            sendBroadcast(intent);
            e.printStackTrace();
        } catch (JSONException e) {
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_BAD_RESPONSE);
            sendBroadcast(intent);
            e.printStackTrace();
        }
    }

    public void getAllScansForManga(String mangaSlug) {
        Intent intent = new Intent(GET_ALL_SCANS_FOR_MANGA);

        if (!isConnected()) {
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_NO_INTERNET);
            sendBroadcast(intent);
            return;
        }

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(String.format(URL_ALL_SCANS_FOR_MANGA, mangaSlug)).openConnection();
            Scanner s = new Scanner(new BufferedInputStream(urlConnection.getInputStream())).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            List<Scan> scans = new ArrayList<>();
            JSONArray tabScan = new JSONArray(result);
            for (int i = 0; i < tabScan.length(); i++) {
                scans.add(new Scan(tabScan.getJSONObject(i).getString("num")));
            }
            intent.putParcelableArrayListExtra(PARAM_SCANS_LIST, (ArrayList<? extends Parcelable>) scans);
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_OK);
            sendBroadcast(intent);
            urlConnection.disconnect();
        } catch (IOException e) {
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_TIMEOUT);
            sendBroadcast(intent);
            e.printStackTrace();
        } catch (JSONException e) {
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_BAD_RESPONSE);
            sendBroadcast(intent);
            e.printStackTrace();
        }
    }

    public void downloadPagesImagesForScan(String mangaSlug, int scanId) {
        boolean isResume = false;
        int initialIndex = 0;
        ContentValues updatedScan = new ContentValues();
        updatedScan.put(ShonenTouchContract.ScanColumns.STATUS, Scan.Status.DOWNLOAD_IN_PROGRESS.name());
        Cursor c = getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(scanId)}, null);
        if (c != null) {
            try {
                if (c.getCount() == 1) {
                    c.moveToFirst();
                    String scanName = c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.NAME));
                    // if download has already been done, don't do it again
                    if (Scan.Status.valueOf(c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.STATUS))) == Scan.Status.DOWNLOAD_COMPLETE) {
                        return;
                    } else if (Scan.Status.valueOf(c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.STATUS))) == Scan.Status.DOWNLOAD_STOPPED) {
                        // get the information to know if this download has been stopped and resumed
                        // in this case, we are resuming download
                        updatedScan.put(ShonenTouchContract.ScanColumns.DOWNLOAD_STATUS, "Reprise du téléchargement...");
                        isResume = true;
                    } else {
                        // in this case, it's the first time we try to download the scan
                        updatedScan.put(ShonenTouchContract.ScanColumns.DOWNLOAD_STATUS, "Téléchargement des url des pages...");
                    }
                    getContentResolver().update(ShonenTouchContract.Scan.CONTENT_URI, updatedScan, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(scanId)});

                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(String.format(URL_ALL_PAGES_FOR_MANGA_AND_SCAN, mangaSlug, scanName)).openConnection();
                    Scanner s = new Scanner(new BufferedInputStream(urlConnection.getInputStream())).useDelimiter("\\A");
                    String result = s.hasNext() ? s.next() : "";
                    List<Page> pages = new ArrayList<>();
                    JSONArray pagesJSONArray = new JSONArray(result);
                    for (int i = 0; i < pagesJSONArray.length(); i++) {
                        pages.add(new Page(pagesJSONArray.getJSONObject(i).getString("num"), pagesJSONArray.getJSONObject(i).getString("url")));
                    }

                    if (isResume) {
                        // read all pages in the table page, related to this scan
                        Cursor pagesCursor = getApplicationContext().getContentResolver().query(ShonenTouchContract.Page.CONTENT_URI, null, ShonenTouchContract.PageColumns.SCAN_ID + "=?", new String[]{ String.valueOf(scanId) }, null);
                        if (pagesCursor != null) {
                            try {
                                for(pagesCursor.moveToFirst(); !pagesCursor.isAfterLast(); pagesCursor.moveToNext()) {
                                    initialIndex++;
                                }
                            } finally {
                                pagesCursor.close();
                            }
                        }
                    }

                    for (int i = initialIndex; i < pages.size(); i++) {
                        try {
                            Bitmap downloadedBitmap = BitmapFactory.decodeStream(new URL(pages.get(i).getPath()).openStream());
                            if (downloadedBitmap != null) {
                                if (!isAllowedToKeepDownloading(scanId)) {
                                    return;
                                }
                                File imageFile = new File(new ContextWrapper(this).getDir(IMAGES_FOLDER_NAME, 0), mangaSlug + HelpFormatter.DEFAULT_OPT_PREFIX + scanId + HelpFormatter.DEFAULT_OPT_PREFIX + i);
                                OutputStream fileOutputStream = new FileOutputStream(imageFile);
                                downloadedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                fileOutputStream.close();
                                ContentValues newPage = new ContentValues();
                                newPage.put(ShonenTouchContract.PageColumns.PATH, imageFile.getAbsolutePath());
                                newPage.put(ShonenTouchContract.PageColumns.SCAN_ID, scanId);
                                getContentResolver().insert(ShonenTouchContract.Page.CONTENT_URI, newPage);
                                updatedScan = new ContentValues();
                                updatedScan.put(ShonenTouchContract.ScanColumns.DOWNLOAD_STATUS, "Téléchargement page " + (i+1) + "/" + pages.size());
                                getContentResolver().update(ShonenTouchContract.Scan.CONTENT_URI, updatedScan, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(scanId)});
                            }
                        } catch (FileNotFoundException e) {
                            updatedScan = new ContentValues();
                            updatedScan.put(ShonenTouchContract.ScanColumns.STATUS, Scan.Status.DOWNLOAD_STOPPED.name());
                            getContentResolver().update(ShonenTouchContract.Scan.CONTENT_URI, updatedScan, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(scanId)});
                        }

                    }
                    updatedScan = new ContentValues();
                    updatedScan.put(ShonenTouchContract.ScanColumns.STATUS, Scan.Status.DOWNLOAD_COMPLETE.name());
                    updatedScan.put(ShonenTouchContract.ScanColumns.DOWNLOAD_TIMESTAMP, System.currentTimeMillis() / 1000);
                    updatedScan.put(ShonenTouchContract.ScanColumns.DOWNLOAD_STATUS, "Téléchargement terminé, disponible hors connexion");
                    getContentResolver().update(ShonenTouchContract.Scan.CONTENT_URI, updatedScan, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(scanId)});
                    urlConnection.disconnect();
                }
            } catch (IOException | JSONException e) {
                updatedScan = new ContentValues();
                updatedScan.put(ShonenTouchContract.ScanColumns.STATUS, Scan.Status.DOWNLOAD_STOPPED.name());
                getContentResolver().update(ShonenTouchContract.Scan.CONTENT_URI, updatedScan, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(scanId)});
                e.printStackTrace();
            } finally {
                c.close();
            }
        }
    }

    private void checkLastScan(String mangaSlug) {
        Intent intent = new Intent(CHECK_LAST_SCAN);

        if (!isConnected()) {
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_NO_INTERNET);
            sendBroadcast(intent);
            return;
        }

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(String.format(URL_SPECIFIC_MANGA, mangaSlug)).openConnection();
            Scanner s = new Scanner(new BufferedInputStream(urlConnection.getInputStream())).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            JSONObject manga = new JSONObject(result);

            intent.putExtra(PARAM_LAST_SCAN, manga.getString("lastScan"));
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_OK);
            sendBroadcast(intent);
            urlConnection.disconnect();
        } catch (IOException e) {
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_TIMEOUT);
            sendBroadcast(intent);
            e.printStackTrace();
        } catch (JSONException e) {
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_BAD_RESPONSE);
            sendBroadcast(intent);
            e.printStackTrace();
        }
    }

    private void getPagesUrlsForScan(final String mangaSlug, final String scanName) {
        Intent intent = new Intent(GET_PAGES_URLS_FOR_SCAN);

        if (!isConnected()) {
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_NO_INTERNET);
            sendBroadcast(intent);
            return;
        }

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(String.format(WSIntentService.URL_ALL_PAGES_FOR_MANGA_AND_SCAN, mangaSlug, scanName)).openConnection();
            Scanner s = new Scanner(new BufferedInputStream(urlConnection.getInputStream())).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            List<String> urls = new ArrayList<>();

            JSONArray pagesJSONArray = new JSONArray(result);

            for (int i = 0; i < pagesJSONArray.length(); i++) {
                urls.add(pagesJSONArray.getJSONObject(i).getString("url"));
            }

            intent.putExtra(PARAM_PAGES_URLS, (ArrayList<String>) urls);
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_OK);
            sendBroadcast(intent);
            urlConnection.disconnect();
        } catch (IOException e) {
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_TIMEOUT);
            sendBroadcast(intent);
            e.printStackTrace();
        } catch (JSONException e) {
            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_BAD_RESPONSE);
            sendBroadcast(intent);
            e.printStackTrace();
        }
    }

//    private void getBitmapPage(final String url) {
//        Intent intent = new Intent(GET_BITMAP_PAGE);
//
//        if (!isConnected()) {
//            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_NO_INTERNET);
//            sendBroadcast(intent);
//            return;
//        }
//
//        try {
//            final Bitmap downloadedBitmap = BitmapFactory.decodeStream(new URL(url).openStream());
//            if (downloadedBitmap != null) {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                downloadedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                intent.putExtra(PARAM_BITMAP, Base64.encodeToString(baos.toByteArray(), 0));
//                intent.putExtra(PARAM_PAGE_URL, url);
//                intent.putExtra(EXTRA_RESULT_CODE, RESULT_OK);
//                sendBroadcast(intent);
//            }
//        } catch (IOException e) {
//            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_TIMEOUT);
//            sendBroadcast(intent);
//            e.printStackTrace();
//        }
//    }

//    private void getBitmapPagesForScan(final String mangaSlug, final int scanId) {
//        int initialIndex = mImagesList.size();
//        Cursor c = getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(scanId)}, null);
//        if (c != null) {
//            try {
//                if (c.getCount() == 1) {
//                    c.moveToFirst();
//                    String scanName = c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.NAME));
//
//                    if (mImagesUrlsList == null) {
//                        mImagesUrlsList = new ArrayList<>();
//                        HttpURLConnection urlConnection = (HttpURLConnection) new URL(String.format(WSIntentService.URL_ALL_PAGES_FOR_MANGA_AND_SCAN, mangaSlug, scanName)).openConnection();
//                        Scanner s = new Scanner(new BufferedInputStream(urlConnection.getInputStream())).useDelimiter("\\A");
//                        String result = s.hasNext() ? s.next() : "";
//
//                        JSONArray pagesJSONArray = new JSONArray(result);
//
//                        for (int i = 0; i < pagesJSONArray.length(); i++) {
//                            mImagesUrlsList.add(pagesJSONArray.getJSONObject(i).getString("url"));
//                        }
//
//                        mProgressBar.setProgress(100 / mImagesUrlsList.size());
//                        mImagesCount = mImagesUrlsList.size();
//                        urlConnection.disconnect();
//                    }
//
//                    for (int i = initialIndex; i < mImagesUrlsList.size(); i++) {
//                        try {
//                            final Bitmap downloadedBitmap = BitmapFactory.decodeStream(new URL(mImagesUrlsList.get(i)).openStream());
//                            if (downloadedBitmap != null) {
//                                // if thread is stopped, do not add image in the list
//                                if (mThreadInterrupted) {
//                                    mThreadInterrupted = false;
//                                    return;
//                                }
//
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                        while (!ALLOWED) {
//
//                                        }
//                                        mImagesList.add(downloadedBitmap);
//                                        System.out.println("************current list size : " + mImagesList.size());
//
//                                        ALLOWED = false;
//
//                                        mExtendedViewPager.getAdapter().notifyDataSetChanged();
////                                                available.release();
//
//
//                                    }
//                                });
//                            }
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            } catch (IOException | JSONException e) {
//                e.printStackTrace();
//            } finally {
//                c.close();
//            }
//        }
//    }

//    private void getBitmapPagesForScan(String mangaSlug, int scanId) {
//        final Intent intent = new Intent(GET_BITMAPS_PAGES_FOR_SCAN);
//
//        int pagesCount = 0;
//
//        if (!isConnected()) {
//            intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_NO_INTERNET);
//            sendBroadcast(intent);
//            return;
//        }
//
//        Cursor c = getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(scanId)}, null);
//        if (c != null) {
//            try {
//                if (c.getCount() == 1) {
//                    c.moveToFirst();
//                    String scanName = c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.NAME));
//
//                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(String.format(URL_ALL_PAGES_FOR_MANGA_AND_SCAN, mangaSlug, scanName)).openConnection();
//                    Scanner s = new Scanner(new BufferedInputStream(urlConnection.getInputStream())).useDelimiter("\\A");
//                    String result = s.hasNext() ? s.next() : "";
//                    List<Page> pages = new ArrayList<>();
//                    JSONArray pagesJSONArray = new JSONArray(result);
//
//                    for (int i = 0; i < pagesJSONArray.length(); i++) {
//                        pages.add(new Page(pagesJSONArray.getJSONObject(i).getString("num"), pagesJSONArray.getJSONObject(i).getString("url")));
//                    }
//
//                    for (int i = 0; i < pages.size(); i++) {
//                        try {
//                            Bitmap downloadedBitmap = BitmapFactory.decodeStream(new URL(pages.get(i).getPath()).openStream());
//                            if (downloadedBitmap != null) {
//                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                                downloadedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                                intent.putExtra(PARAM_BITMAP_AT + pagesCount, stream.toByteArray());
//                                pagesCount++;
//                                intent.putExtra(EXTRA_RESULT_CODE, RESULT_OK);
//                                sendBroadcast(intent);
//                            }
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    urlConnection.disconnect();
////                    intent.putExtra(PARAM_BITMAPS_LIST_SIZE, pagesCount);
////                    intent.putExtra(EXTRA_RESULT_CODE, RESULT_OK);
////                    sendBroadcast(intent);
//                }
//            } catch (IOException e) {
//                intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_TIMEOUT);
//                sendBroadcast(intent);
//                e.printStackTrace();
//            } catch (JSONException e) {
//                intent.putExtra(EXTRA_RESULT_CODE, RESULT_ERROR_BAD_RESPONSE);
//                sendBroadcast(intent);
//                e.printStackTrace();
//            } finally {
//                c.close();
//            }
//        }
//    }

    private boolean isAllowedToKeepDownloading(int scanId) {
        Cursor c = getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(scanId)}, null);
        if (c != null) {
            try {
                if (c.getCount() == 1) {
                    c.moveToFirst();
                    if (Scan.Status.valueOf(c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.STATUS))) == Scan.Status.DOWNLOAD_STOPPED) {
                        return false;
                    } else {
                        return true;
                    }
                }
            } finally {
                c.close();
            }
        }

        return false;
    }

    /**
     * Indicates whether network connectivity exists.
     * @return true if network connectivity exists, false otherwise.
     */
    private boolean isConnected() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}