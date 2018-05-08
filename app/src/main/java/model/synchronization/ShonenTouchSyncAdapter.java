package model.synchronization;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;

import org.apache.commons.cli.HelpFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.github.senerh.shonentouch.R;
import model.database.ShonenTouchContract;
import model.entities.Manga;
import model.services.WSIntentService;
import ui.activities.HomeActivity;
import ui.activities.MangaActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ShonenTouchSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final int MAX_NOTIFICATIONS_IN_A_ROW = 3;

    public ShonenTouchSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public ShonenTouchSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, final Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
//        System.out.println("******************");
//        System.out.println("******************");
//        System.out.println("sync shonen touch");
//        System.out.println("******************");
//        System.out.println("******************");
//        System.out.println("******************");
        try {
            URL url = new URL(WSIntentService.URL_ALL_MANGA);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner scanner = new Scanner(in).useDelimiter("\\A");
            String result = scanner.hasNext() ? scanner.next() : "";

            List<Manga> mangasFromServer = new ArrayList<>();
            List<Manga> mangasFromDb = new ArrayList<>();
            JSONArray tabManga = new JSONArray(result);

            for (int i = 0; i < tabManga.length(); i++) {
                JSONObject currentManga = tabManga.getJSONObject(i);
                mangasFromServer.add(new Manga(currentManga.getString("name"), currentManga.getString("slug"), currentManga.getString("lastScan"), currentManga.getString("url"), false));
            }

            Cursor c = getContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns.FAVORITE + "=?", new String[]{ "1" }, null);
            if (c != null) {
                try {
                    for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        mangasFromDb.add(new Manga(
                                c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.NAME)),
                                c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.SLUG)),
                                c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.LAST_SCAN)),
                                c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.ICON_PATH)),
                                true)
                        );
//                        System.out.println("**********favorite : " + c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.NAME)));
                    }
                } finally {
                    c.close();
                }
            }

            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
            List<Notification> notificationsList = new ArrayList<>();

            // count how many notifications we have to do
            for (int i = 0; i < mangasFromDb.size(); i++) {
                for (int j = 0; j < mangasFromServer.size(); j++) {
                    if (mangasFromDb.get(i).getSlug().equals(mangasFromServer.get(j).getSlug())) {
                        if (!mangasFromDb.get(i).getLastScan().equals(mangasFromServer.get(j).getLastScan())) {
//                            Bitmap notificationIcon;
//                            if (!"".equals(mangasFromDb.get(i).getIconPath())) {
//                                BitmapFactory.Options options = new BitmapFactory.Options();
//                                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                                notificationIcon = BitmapFactory.decodeFile(mangasFromDb.get(i).getIconPath(), options);
//                            } else {
//                                notificationIcon = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher);
//                            }
                            // Handle up navigation to go to home activity
                            final Intent upNavigationIntent = new Intent(getContext(), HomeActivity.class);
                            upNavigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            // prepare intent which is triggered if the notification is clicked
                            Intent notifIntent = new Intent(getContext(), MangaActivity.class);
                            notifIntent.putExtra("mangaId", getMangaId(mangasFromDb.get(i)));
                            notifIntent.putExtra("mangaName", mangasFromDb.get(i).getName());
                            // use System.currentTimeMillis() to have a unique ID for the pending intent
                            PendingIntent pIntent = PendingIntent.getActivities(getContext(), (int) System.currentTimeMillis(),
                                    new Intent[]{upNavigationIntent, notifIntent}, PendingIntent.FLAG_ONE_SHOT);

                            Notification n  = new Notification.Builder(getContext())
                                    .setContentTitle(getContext().getString(R.string.notification_title))
                                    .setContentText(getContext().getString(R.string.notification_content, mangasFromServer.get(j).getName(), mangasFromServer.get(j).getLastScan()))
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentIntent(pIntent)
                                    .setAutoCancel(true)
                                    .build();

                            n.flags |= Notification.FLAG_AUTO_CANCEL;

                            notificationsList.add(n);
                        }
                    }
                }
            }

            // if there are too many notifications, trigger a single generic one
            if (notificationsList.size() > MAX_NOTIFICATIONS_IN_A_ROW) {
                // prepare intent which is triggered if the notification is clicked
                Intent intent = new Intent(getContext(), HomeActivity.class);
                // use System.currentTimeMillis() to have a unique ID for the pending intent
                PendingIntent pIntent = PendingIntent.getActivity(getContext(), (int) System.currentTimeMillis(), intent, 0);

                Notification n  = new Notification.Builder(getContext())
                        .setContentTitle(getContext().getString(R.string.notification_title))
                        .setContentText(getContext().getString(R.string.notification_generic_content))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .build();

                n.flags |= Notification.FLAG_AUTO_CANCEL;

                notificationManager.notify(0, n);
            } else {
                for (int i = 0; i < notificationsList.size(); i++) {
//                    System.out.println("****************trigger notif");
                    notificationManager.notify(i, notificationsList.get(i));
                }
            }

            urlConnection.disconnect();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private int getMangaId(Manga manga) {
        Cursor c = getContext().getContentResolver().query(ShonenTouchContract.Manga.CONTENT_URI, null, ShonenTouchContract.MangaColumns.SLUG + "=?", new String[]{ manga.getSlug() }, null);
        if (c != null && c.getCount() == 1) {
            try {
                c.moveToFirst();
                return c.getInt(c.getColumnIndex(ShonenTouchContract.MangaColumns._ID));
            } finally {
                c.close();
            }
        }

        return -1;
    }
}