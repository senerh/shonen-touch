package model.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import org.apache.commons.cli.HelpFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.database.ShonenTouchContract;
import model.entities.Manga;
import model.entities.Scan;


/**
 * Created by Thibaut SORIANO on 28/03/2017.
 */

public class WSIntentService extends IntentService {

    public static final String URL_SERVER = "http://senerh.xyz:8080/shonen-touch-api/";
    public static final String GET_ALL_MANGA = URL_SERVER + "mangas";
    public static final String GET_ALL_PAGES_FOR_MANGA_AND_SCAN = "http://senerh.xyz:8080/shonen-touch-api/mangas/%1$s/scans/%2$s/pages";
    public static final String GET_ALL_SCANS_FOR_MANGA = "http://senerh.xyz:8080/shonen-touch-api/mangas/%1$s/scans";
    public static final String GET_URL_FOR_PAGE = "http://senerh.xyz:8080/shonen-touch-api/mangas/%1$s/scans/%2$s/pages/%3$s/image";
    public static final String DOWNLOAD_PAGES_FOR_SCAN = "DOWNLOAD_PAGES_FOR_SCAN";

    private static final String IMAGES_FOLDER_NAME = "shonentouch";

    public static final String PARAM_MANGAS_LIST = "mangasList";
    public static final String PARAM_MANGA_SLUG = "mangaSlug";
    public static final String PARAM_PAGES_LIST = "pagesList";
    public static final String PARAM_SCANS_LIST = "scansList";
    public static final String PARAM_SCAN_ID = "scanId";

    public WSIntentService() {
        super("WSIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        switch (intent.getAction()) {
            case GET_ALL_MANGA:
                getAllManga();
                break;
            case GET_ALL_SCANS_FOR_MANGA:
                if (!"".equals(intent.getStringExtra(PARAM_MANGA_SLUG))) {
                    getAllScansForManga(intent.getStringExtra(PARAM_MANGA_SLUG));
                }
                break;
            case DOWNLOAD_PAGES_FOR_SCAN:
                if (!"".equals(intent.getStringExtra(PARAM_MANGA_SLUG)) && intent.getLongExtra(PARAM_SCAN_ID, -1) != -1) {
                    downloadPagesImagesForScan(intent.getStringExtra(PARAM_MANGA_SLUG), intent.getLongExtra(PARAM_SCAN_ID, -1));
                }
                break;
            default:
                break;
        }
    }

    public void getAllManga() {
        final Intent intent = new Intent(GET_ALL_MANGA);

        try {
            URL url = new URL(GET_ALL_MANGA);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";

            List<Manga> mangas = new ArrayList<>();
            JSONArray tabManga = new JSONArray(result);

            for (int i = 0; i < tabManga.length(); i++) {
                JSONObject currentManga = tabManga.getJSONObject(i);
                mangas.add(new Manga(currentManga.getString("name"), currentManga.getString("slug")));
            }

            intent.putParcelableArrayListExtra(PARAM_MANGAS_LIST, (ArrayList<? extends Parcelable>) mangas);
            System.out.println("\n\n************** response ***************************\n" + result + "\n\n");
            sendBroadcast(intent);

            urlConnection.disconnect();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAllScansForManga(String mangaSlug) {
        Intent intent = new Intent(GET_ALL_SCANS_FOR_MANGA);
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(String.format(GET_ALL_SCANS_FOR_MANGA, mangaSlug)).openConnection();
            Scanner s = new Scanner(new BufferedInputStream(urlConnection.getInputStream())).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            List<Scan> scans = new ArrayList<>();
            JSONArray tabScan = new JSONArray(result);
            for (int i = 0; i < tabScan.length(); i++) {
                scans.add(new Scan(tabScan.getJSONObject(i).getString("num")));
            }
            intent.putParcelableArrayListExtra(PARAM_SCANS_LIST, (ArrayList<? extends Parcelable>) scans);
            System.out.println("\n\n************** response ***************************\n" + result + "\n\n");
            sendBroadcast(intent);
            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void downloadPagesImagesForScan(String mangaSlug, long scanId) {
        ContentValues updatedScan= new ContentValues();
        updatedScan.put(ShonenTouchContract.ScanColumns.STATUS, Scan.Status.DOWNLOAD_IN_PROGRESS.name());
        getContentResolver().update(ShonenTouchContract.Scan.CONTENT_URI, updatedScan, "_id=?", new String[]{String.valueOf(scanId)});
        Cursor c = getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, "_id=?", new String[]{String.valueOf(scanId)}, null);
        if (c != null) {
            try {
                if (c.getCount() == 1) {
                    c.moveToFirst();
                    String scanName = c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.NAME));
                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(String.format(GET_ALL_PAGES_FOR_MANGA_AND_SCAN, mangaSlug, scanName)).openConnection();
                    Scanner s = new Scanner(new BufferedInputStream(urlConnection.getInputStream())).useDelimiter("\\A");
                    String result = s.hasNext() ? s.next() : "";
                    System.out.println("\n\n************** response ***************************\n" + result + "\n\n");
                    List<String> pages = new ArrayList<>();
                    JSONArray jSONArray = new JSONArray(result);
                    for (int i = 0; i < jSONArray.length(); i++) {
                        pages.add(jSONArray.getJSONObject(i).getString("num"));
                    }
                    List<String> pagesUrls = new ArrayList<>();
                    for (int i = 0; i < pages.size(); i++) {
                        Scanner sPage = new Scanner(new BufferedInputStream(new URL(String.format(GET_URL_FOR_PAGE, mangaSlug, scanName, pages.get(i))).openConnection().getInputStream())).useDelimiter("\\A");
                        String resultPage = sPage.hasNext() ? sPage.next() : "";
                        System.out.println("\n\n************** response ***************************\n" + resultPage + "\n\n");
                        pagesUrls.add(new JSONObject(resultPage).getString("url"));
                    }
                    for (int i = 0; i < pagesUrls.size(); i++) {
                        if (!(pagesUrls.get(i)).contains("__")) {
                            Bitmap downloadedBitmap = BitmapFactory.decodeStream(new URL(pagesUrls.get(i)).openStream());
                            if (downloadedBitmap != null) {
                                File imageFile = new File(new ContextWrapper(this).getDir(IMAGES_FOLDER_NAME, 0), mangaSlug + HelpFormatter.DEFAULT_OPT_PREFIX + scanId + HelpFormatter.DEFAULT_OPT_PREFIX + i);
                                OutputStream fileOutputStream = new FileOutputStream(imageFile);
                                downloadedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                fileOutputStream.close();
                                ContentValues newPage = new ContentValues();
                                newPage.put(ShonenTouchContract.PageColumns.PATH, imageFile.getAbsolutePath());
                                newPage.put(ShonenTouchContract.PageColumns.SCAN_ID, scanId);
                                getContentResolver().insert(ShonenTouchContract.Page.CONTENT_URI, newPage);
                            }
                        }
                    }
                    updatedScan = new ContentValues();
                    updatedScan.put(ShonenTouchContract.ScanColumns.STATUS, Scan.Status.DOWNLOAD_COMPLETE.name());
                    updatedScan.put(ShonenTouchContract.ScanColumns.DOWNLOAD_TIMESTAMP, System.currentTimeMillis() / 1000);
                    getContentResolver().update(ShonenTouchContract.Scan.CONTENT_URI, updatedScan, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(scanId)});
                    urlConnection.disconnect();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                c.close();
            }
        }
    }
}