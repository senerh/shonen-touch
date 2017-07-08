package model.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by Thibaut SORIANO on 04/04/2017.
 */
public class ShonenTouchProvider extends ContentProvider {
    // Database
    protected ShonenTouchDatabase mDatabase;

    // URI matcher
    protected UriMatcher mUriMatcher;

    // Content URI IDs
    private static final int MANGA = 100;
    private static final int MANGA_ID = 101;

    @Override
    public boolean onCreate() {
        mDatabase = new ShonenTouchDatabase(getContext());
        final String authority = ShonenTouchContract.AUTHORITY;
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(authority, ShonenTouchContract.Manga.TABLE_NAME, MANGA);
        mUriMatcher.addURI(authority, ShonenTouchContract.Manga.TABLE_NAME + "/#", MANGA_ID);

        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int uriType = mUriMatcher.match(uri);

        switch(uriType){
            case MANGA:
                return ShonenTouchContract.Manga.CONTENT_TYPE;
            case MANGA_ID:
                return ShonenTouchContract.Manga.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase database = mDatabase.getWritableDatabase();
        final int uriType = mUriMatcher.match(uri);

        switch (uriType) {
            case MANGA: {
                long rowId = database.insert(ShonenTouchContract.Manga.TABLE_NAME, ShonenTouchContract.MangaColumns.NAME, values);
                if (rowId != -1) {
                    final Uri rowUri = ContentUris.withAppendedId(ShonenTouchContract.Manga.CONTENT_ID_URI, rowId);
                    getContext().getContentResolver().notifyChange(rowUri, null);
                    return rowUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            }
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = mDatabase.getWritableDatabase();
        final int uriType = mUriMatcher.match(uri);
        int rowsAffected;
        switch (uriType) {
            case MANGA: {
                rowsAffected = database.update(ShonenTouchContract.Manga.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case MANGA_ID: {
                final String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = database.update(ShonenTouchContract.Manga.TABLE_NAME, values, ShonenTouchContract.MangaColumns._ID + " = " + id, null);
                } else {
                    rowsAffected = database.update(ShonenTouchContract.Manga.TABLE_NAME, values, selection + " AND " + ShonenTouchContract.MangaColumns._ID + " = " + id, selectionArgs);
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = mDatabase.getWritableDatabase();
        final int uriType = mUriMatcher.match(uri);
        int rowsAffected;

        switch (uriType) {
            case MANGA: {
                rowsAffected = database.delete(ShonenTouchContract.Manga.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case MANGA_ID: {
                final String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = database.delete(ShonenTouchContract.Manga.TABLE_NAME, ShonenTouchContract.MangaColumns._ID + " = " + id, null);
                } else {
                    rowsAffected = database.delete(ShonenTouchContract.Manga.TABLE_NAME, selection + " AND " + ShonenTouchContract.MangaColumns._ID + " = " + id, selectionArgs);
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase database = mDatabase.getReadableDatabase();
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        final int uriType = mUriMatcher.match(uri);

        switch (uriType) {
            case MANGA: {
                queryBuilder.setTables(ShonenTouchContract.Manga.TABLE_NAME);
                break;
            }
            case MANGA_ID: {
                queryBuilder.setTables(ShonenTouchContract.Manga.TABLE_NAME);
                queryBuilder.appendWhere(ShonenTouchContract.MangaColumns._ID + " = " + uri.getLastPathSegment());
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }

        final Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
}
