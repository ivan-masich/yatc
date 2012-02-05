package com.masich.yatc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * SQLiteDbHelper for time_line.db database.
 *
 * @author Masich Ivan <john@masich.com>
 */
public class TimeLineDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "time_line.db";
    public static final int    DB_VERSION = 1;
    public static final String TABLE_NAME = "time_line";
    public static final String C_ID = BaseColumns._ID;
    public static final String C_CREATED_AT = "created_at";
    public static final String C_TEXT = "status";
    public static final String C_USER = "user";

    public TimeLineDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format(
            "CREATE table %s ( " +
            "%s INTEGER NOT NULL PRIMARY KEY," +
            "%s INTEGER, %s TEXT, %s TEXT)",
            TABLE_NAME, C_ID, C_CREATED_AT, C_TEXT, C_USER
        );

        db.execSQL(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onCreate(db);
    }
}
