package com.masich.yatc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import twitter4j.Status;

/**
 * Model to work with time line content provider.
 *
 * @author Masich Ivan <john@masich.com>
 */
public class TimeLineModel {
    private Context context;

    public TimeLineModel(Context context) {
        this.context = context;
    }

    /**
     * Get all time line statuses.
     *
     * @return {@link Cursor} with all time line statuses.
     */
    public Cursor getAll() {
        return context.getContentResolver().query(
            TimeLineProvider.URI,
            null, null, null,
            TimeLineDbHelper.C_CREATED_AT + " DESC"
        );
    }

    /**
     * Check if status exist in database.
     *
     * @param status {@link Status} from twitter4j client.
     * @return true if exist and false in not.
     */
    public boolean checkExist(Status status) {
        Cursor cursor = context.getContentResolver().query(
            TimeLineProvider.URI,
            null,
            TimeLineDbHelper.C_ID + " = " + status.getId(),
            null,
            null
        );

        return cursor.getCount() > 0;
    }

    /**
     * Insert new status in database.
     *
     * @param status {@link Status} from twitter4j client.
     */
    public void insert(Status status) {
        ContentValues values = new ContentValues();

        values.put(TimeLineDbHelper.C_ID, status.getId());
        values.put(TimeLineDbHelper.C_CREATED_AT, status.getCreatedAt().getTime());
        values.put(TimeLineDbHelper.C_TEXT, status.getText());
        values.put(TimeLineDbHelper.C_USER, status.getUser().getScreenName());

        context.getContentResolver().insert(TimeLineProvider.URI, values);
    }
}
