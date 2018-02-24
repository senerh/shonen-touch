package model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Thibaut SORIANO on 29/03/2017.
 */

public class ShonenTouchDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Manga.db";

    // Tables
    private interface Tables {
        /**
         * The SQL statement that creates the plug table.
         */
        String CREATE_MANGA =
                "CREATE TABLE " + ShonenTouchContract.Manga.TABLE_NAME + " (" +
                        ShonenTouchContract.MangaColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        ShonenTouchContract.MangaColumns.NAME + " TEXT, " +
                        ShonenTouchContract.MangaColumns.SLUG + " TEXT UNIQUE);";

        String CREATE_SCAN =
                "CREATE TABLE " + ShonenTouchContract.Scan.TABLE_NAME + " (" +
                        ShonenTouchContract.ScanColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        ShonenTouchContract.ScanColumns.NAME + " TEXT, " +
                        ShonenTouchContract.ScanColumns.DOWNLOAD_TIMESTAMP + " BIGINT, " +
                        ShonenTouchContract.ScanColumns.LAST_READ_PAGE + " INTEGER, " +
                        ShonenTouchContract.ScanColumns.STATUS + " TEXT NOT NULL, " +
                        ShonenTouchContract.ScanColumns.MANGA_ID + " BIGINT," +
                        "FOREIGN KEY (" + ShonenTouchContract.ScanColumns.MANGA_ID + ")" +
                        "REFERENCES " + ShonenTouchContract.Manga.TABLE_NAME + " (" + ShonenTouchContract.MangaColumns._ID + ") ON DELETE CASCADE);";

        String CREATE_PAGE =
                "CREATE TABLE " + ShonenTouchContract.Page.TABLE_NAME + " (" +
                        ShonenTouchContract.PageColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        ShonenTouchContract.PageColumns.PATH + " TEXT UNIQUE, " +
                        ShonenTouchContract.PageColumns.SCAN_ID + " BIGINT," +
                        "FOREIGN KEY (" + ShonenTouchContract.PageColumns.SCAN_ID + ")" +
                        "REFERENCES " + ShonenTouchContract.Scan.TABLE_NAME + " (" + ShonenTouchContract.ScanColumns._ID + ") ON DELETE CASCADE);";
    }

    public ShonenTouchDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Tables.CREATE_MANGA);
        db.execSQL(Tables.CREATE_SCAN);
        db.execSQL(Tables.CREATE_PAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
        super.onOpen(db);
    }
}
