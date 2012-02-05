package com.masich.yatc.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Time line content provider.
 *
 * @author Masich Ivan <john@masich.com>
 */
public class TimeLineProvider extends ContentProvider {
    public static Uri URI = Uri.parse("content://com.masich.yatc.db.timelineprovider");
    private SQLiteDatabase db;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreate() {
        db = (new TimeLineDbHelper(getContext())).getWritableDatabase();

        return (db != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        Cursor cursor = db.query(TimeLineDbHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sort);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        ContentValues values = new ContentValues(contentValues);

        long rowId = db.insert(TimeLineDbHelper.TABLE_NAME, null, values);

        if (rowId > 0) {
            Uri newRowUri = ContentUris.withAppendedId(URI, rowId);
            getContext().getContentResolver().notifyChange(newRowUri, null);

            return newRowUri;
        }
        else {
            throw new SQLException(String.format("Failed to insert row into %s", uri));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int retVal = db.delete(TimeLineDbHelper.TABLE_NAME, where, whereArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        int retVal = db.update(TimeLineDbHelper.TABLE_NAME, values, where, whereArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return retVal;
    }
}
