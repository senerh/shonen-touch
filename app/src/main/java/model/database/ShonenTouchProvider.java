//package model.database;
//
//import android.content.ContentProvider;
//import android.content.ContentUris;
//import android.content.ContentValues;
//import android.content.UriMatcher;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteQueryBuilder;
//import android.net.Uri;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//
///**
// * Created by Thibaut SORIANO on 04/04/2017.
// */
//public class ShonenTouchSyncProvider extends ContentProvider {
//    // Database
//    protected ShonenTouchDatabase mDatabase;
//
//    // URI matcher
//    protected UriMatcher mUriMatcher;
//
//    // Content URI IDs
//    private static final int MANGA = 100;
//    private static final int MANGA_ID = 101;
//
//    @Override
//    public boolean onCreate() {
//        mDatabase = new ShonenTouchDatabase(getContext());
//        final String authority = ShonenTouchContract.AUTHORITY;
//        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//        mUriMatcher.addURI(authority, ShonenTouchContract.Manga.TABLE_NAME, MANGA);
//        mUriMatcher.addURI(authority, ShonenTouchContract.Manga.TABLE_NAME + "/#", MANGA_ID);
//
//        return true;
//    }
//
//    @Nullable
//    @Override
//    public String getType(@NonNull Uri uri) {
//        final int uriType = mUriMatcher.match(uri);
//
//        switch(uriType){
//            case MANGA:
//                return ShonenTouchContract.Manga.CONTENT_TYPE;
//            case MANGA_ID:
//                return ShonenTouchContract.Manga.CONTENT_ITEM_TYPE;
//            default:
//                throw new IllegalArgumentException("Unknown URI: " + uri);
//        }
//    }
//
//    @Nullable
//    @Override
//    public Uri insert(@NonNull Uri uri, ContentValues values) {
//        final SQLiteDatabase database = mDatabase.getWritableDatabase();
//        final int uriType = mUriMatcher.match(uri);
//
//        switch (uriType) {
//            case MANGA: {
//                long rowId = database.insert(ShonenTouchContract.Manga.TABLE_NAME, ShonenTouchContract.MangaColumns.NAME, values);
//                if (rowId != -1) {
//                    final Uri rowUri = ContentUris.withAppendedId(ShonenTouchContract.Manga.CONTENT_ID_URI, rowId);
//                    getContext().getContentResolver().notifyChange(rowUri, null);
//                    return rowUri;
//                }
//                throw new SQLException("Failed to insert row into " + uri);
//            }
//            default: {
//                throw new IllegalArgumentException("Unknown URI: " + uri);
//            }
//        }
//    }
//
//    @Override
//    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//        final SQLiteDatabase database = mDatabase.getWritableDatabase();
//        final int uriType = mUriMatcher.match(uri);
//        int rowsAffected;
//        switch (uriType) {
//            case MANGA: {
//                rowsAffected = database.update(ShonenTouchContract.Manga.TABLE_NAME, values, selection, selectionArgs);
//                break;
//            }
//            case MANGA_ID: {
//                final String id = uri.getLastPathSegment();
//                if (TextUtils.isEmpty(selection)) {
//                    rowsAffected = database.update(ShonenTouchContract.Manga.TABLE_NAME, values, ShonenTouchContract.MangaColumns._ID + " = " + id, null);
//                } else {
//                    rowsAffected = database.update(ShonenTouchContract.Manga.TABLE_NAME, values, selection + " AND " + ShonenTouchContract.MangaColumns._ID + " = " + id, selectionArgs);
//                }
//                break;
//            }
//            default: {
//                throw new IllegalArgumentException("Unknown URI: " + uri);
//            }
//        }
//        getContext().getContentResolver().notifyChange(uri, null);
//        return rowsAffected;
//    }
//
//    @Override
//    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
//        final SQLiteDatabase database = mDatabase.getWritableDatabase();
//        final int uriType = mUriMatcher.match(uri);
//        int rowsAffected;
//
//        switch (uriType) {
//            case MANGA: {
//                rowsAffected = database.delete(ShonenTouchContract.Manga.TABLE_NAME, selection, selectionArgs);
//                break;
//            }
//            case MANGA_ID: {
//                final String id = uri.getLastPathSegment();
//                if (TextUtils.isEmpty(selection)) {
//                    rowsAffected = database.delete(ShonenTouchContract.Manga.TABLE_NAME, ShonenTouchContract.MangaColumns._ID + " = " + id, null);
//                } else {
//                    rowsAffected = database.delete(ShonenTouchContract.Manga.TABLE_NAME, selection + " AND " + ShonenTouchContract.MangaColumns._ID + " = " + id, selectionArgs);
//                }
//                break;
//            }
//            default: {
//                throw new IllegalArgumentException("Unknown URI: " + uri);
//            }
//        }
//        getContext().getContentResolver().notifyChange(uri, null);
//        return rowsAffected;
//    }
//
//    @Nullable
//    @Override
//    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        final SQLiteDatabase database = mDatabase.getReadableDatabase();
//        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
//        final int uriType = mUriMatcher.match(uri);
//
//        switch (uriType) {
//            case MANGA: {
//                queryBuilder.setTables(ShonenTouchContract.Manga.TABLE_NAME);
//                break;
//            }
//            case MANGA_ID: {
//                queryBuilder.setTables(ShonenTouchContract.Manga.TABLE_NAME);
//                queryBuilder.appendWhere(ShonenTouchContract.MangaColumns._ID + " = " + uri.getLastPathSegment());
//                break;
//            }
//            default: {
//                throw new IllegalArgumentException("Unknown URI: " + uri);
//            }
//        }
//
//        final Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
//        cursor.setNotificationUri(getContext().getContentResolver(), uri);
//        return cursor;
//    }
//}
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
    private static final int SCAN = 200;
    private static final int SCAN_ID = 201;
    private static final int PAGE = 300;
    private static final int PAGE_ID = 301;

    @Override
    public boolean onCreate() {
        mDatabase = new ShonenTouchDatabase(getContext());
        final String authority = ShonenTouchContract.AUTHORITY;
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(authority, ShonenTouchContract.Manga.TABLE_NAME, MANGA);
        mUriMatcher.addURI(authority, ShonenTouchContract.Manga.TABLE_NAME + "/#", MANGA_ID);
        mUriMatcher.addURI(authority, ShonenTouchContract.Scan.TABLE_NAME, SCAN);
        mUriMatcher.addURI(authority, ShonenTouchContract.Scan.TABLE_NAME + "/#", SCAN_ID);
        mUriMatcher.addURI(authority, ShonenTouchContract.Page.TABLE_NAME, PAGE);
        mUriMatcher.addURI(authority, ShonenTouchContract.Page.TABLE_NAME + "/#", PAGE_ID);

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
            case SCAN:
                return ShonenTouchContract.Scan.CONTENT_TYPE;
            case SCAN_ID:
                return ShonenTouchContract.Scan.CONTENT_ITEM_TYPE;
            case PAGE:
                return ShonenTouchContract.Page.CONTENT_TYPE;
            case PAGE_ID:
                return ShonenTouchContract.Page.CONTENT_ITEM_TYPE;
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
            case SCAN: {
                long rowId = database.insert(ShonenTouchContract.Scan.TABLE_NAME, ShonenTouchContract.ScanColumns.NAME, values);
                if (rowId != -1) {
                    final Uri rowUri = ContentUris.withAppendedId(ShonenTouchContract.Scan.CONTENT_ID_URI, rowId);
                    getContext().getContentResolver().notifyChange(rowUri, null);
                    return rowUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            }
            case PAGE: {
                long rowId = database.insert(ShonenTouchContract.Page.TABLE_NAME, ShonenTouchContract.PageColumns.PATH, values);
                if (rowId != -1) {
                    final Uri rowUri = ContentUris.withAppendedId(ShonenTouchContract.Page.CONTENT_ID_URI, rowId);
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
            case SCAN: {
                rowsAffected = database.update(ShonenTouchContract.Scan.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case SCAN_ID: {
                final String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = database.update(ShonenTouchContract.Scan.TABLE_NAME, values, ShonenTouchContract.ScanColumns._ID + " = " + id, null);
                } else {
                    rowsAffected = database.update(ShonenTouchContract.Scan.TABLE_NAME, values, selection + " AND " + ShonenTouchContract.ScanColumns._ID + " = " + id, selectionArgs);
                }
                break;
            }
            case PAGE: {
                rowsAffected = database.update(ShonenTouchContract.Page.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case PAGE_ID: {
                final String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = database.update(ShonenTouchContract.Page.TABLE_NAME, values, ShonenTouchContract.PageColumns._ID + " = " + id, null);
                } else {
                    rowsAffected = database.update(ShonenTouchContract.Page.TABLE_NAME, values, selection + " AND " + ShonenTouchContract.PageColumns._ID + " = " + id, selectionArgs);
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
            case SCAN: {
                rowsAffected = database.delete(ShonenTouchContract.Scan.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case SCAN_ID: {
                final String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = database.delete(ShonenTouchContract.Scan.TABLE_NAME, ShonenTouchContract.ScanColumns._ID + " = " + id, null);
                } else {
                    rowsAffected = database.delete(ShonenTouchContract.Scan.TABLE_NAME, selection + " AND " + ShonenTouchContract.ScanColumns._ID + " = " + id, selectionArgs);
                }
                break;
            }
            case PAGE: {
                rowsAffected = database.delete(ShonenTouchContract.Page.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case PAGE_ID: {
                final String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = database.delete(ShonenTouchContract.Page.TABLE_NAME, ShonenTouchContract.PageColumns._ID + " = " + id, null);
                } else {
                    rowsAffected = database.delete(ShonenTouchContract.Page.TABLE_NAME, selection + " AND " + ShonenTouchContract.PageColumns._ID + " = " + id, selectionArgs);
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
            case SCAN: {
                queryBuilder.setTables(ShonenTouchContract.Scan.TABLE_NAME);
                break;
            }
            case SCAN_ID: {
                queryBuilder.setTables(ShonenTouchContract.Scan.TABLE_NAME);
                queryBuilder.appendWhere(ShonenTouchContract.ScanColumns._ID + " = " + uri.getLastPathSegment());
                break;
            }
            case PAGE: {
                queryBuilder.setTables(ShonenTouchContract.Page.TABLE_NAME);
                break;
            }
            case PAGE_ID: {
                queryBuilder.setTables(ShonenTouchContract.Page.TABLE_NAME);
                queryBuilder.appendWhere(ShonenTouchContract.PageColumns._ID + " = " + uri.getLastPathSegment());
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
