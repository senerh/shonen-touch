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
                        ShonenTouchContract.MangaColumns._ID + " INTEGER PRIMARY KEY, " +
                        ShonenTouchContract.MangaColumns.NAME + " TEXT NOT NULL UNIQUE, " +
                        ShonenTouchContract.MangaColumns.SLUG + " TEXT);";
    }

    public ShonenTouchDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.setForeignKeyConstraintsEnabled(true);
        db.execSQL(Tables.CREATE_MANGA);
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
