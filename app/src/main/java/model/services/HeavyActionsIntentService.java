package model.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;

import java.io.File;

import model.database.ShonenTouchContract;
import model.entities.Scan;


/**
 * Created by Thibaut SORIANO on 5/04/2018.
 */
public class HeavyActionsIntentService extends IntentService {
    public static final String DELETE_SCAN_PAGES = "DELETE_SCAN_PAGES";

    public static final String EXTRA_SCAN_ID = "scanId";

    public HeavyActionsIntentService() {
        super("HeavyActionsIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        switch (intent.getAction()) {
            case DELETE_SCAN_PAGES:
                deleteScanPages(intent.getIntExtra(EXTRA_SCAN_ID, -1));
                break;
            default:
                break;
        }
    }

    public void deleteScanPages(int scanId) {
        ContentValues updatedScan;
        if (scanId != -1 && isDeletable(scanId)) {
            Cursor c = getApplicationContext().getContentResolver().query(ShonenTouchContract.Page.CONTENT_URI, null, ShonenTouchContract.PageColumns.SCAN_ID + "=?", new String[]{ String.valueOf(scanId) }, null);
            if (c != null) {
                try {
                    // inform user that deletion is being performed
                    updatedScan = new ContentValues();
                    updatedScan.put(ShonenTouchContract.ScanColumns.DOWNLOAD_STATUS, "Suppression des pages en cours...");
                    getApplicationContext().getContentResolver().update(ShonenTouchContract.Scan.CONTENT_URI, updatedScan, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(scanId)});
                    for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        File imageFile = new File(c.getString(c.getColumnIndex(ShonenTouchContract.PageColumns.PATH)));
                        boolean fileDeleted = imageFile.delete();
                        if (fileDeleted) {
                            String selection = ShonenTouchContract.PageColumns._ID + " = ?";
                            String[] selectionArgs = { c.getString(c.getColumnIndex(ShonenTouchContract.PageColumns._ID)) };
                            getApplicationContext().getContentResolver().delete(ShonenTouchContract.Page.CONTENT_URI, selection, selectionArgs);
                        }
                    }
                    updatedScan = new ContentValues();
                    updatedScan.put(ShonenTouchContract.ScanColumns.STATUS, Scan.Status.NOT_DOWNLOADED.name());
                    updatedScan.put(ShonenTouchContract.ScanColumns.DOWNLOAD_STATUS, "");
                    getApplicationContext().getContentResolver().update(ShonenTouchContract.Scan.CONTENT_URI, updatedScan, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{String.valueOf(scanId)});
                } finally {
                    c.close();
                }
            }
        }
    }

    private boolean isDeletable(int scanId) {
        Cursor c = getApplicationContext().getContentResolver().query(ShonenTouchContract.Scan.CONTENT_URI, null, ShonenTouchContract.ScanColumns._ID + "=?", new String[]{ String.valueOf(scanId) }, null);
        if (c != null) {
            try {
                if (c.getCount() == 1) {
                    c.moveToFirst();
                    if (Scan.Status.valueOf(c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.STATUS))) == Scan.Status.NOT_DOWNLOADED) {
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
}