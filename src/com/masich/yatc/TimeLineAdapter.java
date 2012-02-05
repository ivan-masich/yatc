package com.masich.yatc;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.masich.yatc.db.TimeLineDbHelper;

/**
 * Adapter for time line {@link android.widget.ListView}.
 *
 * @author Masich Ivan <john@masich.com>
 */
public class TimeLineAdapter extends SimpleCursorAdapter {
    static final String[] from = {
        TimeLineDbHelper.C_USER,
        TimeLineDbHelper.C_TEXT
    };

    static final int[] to = {
        R.id.textUser,
        R.id.textText
    };

    public TimeLineAdapter(Context context, Cursor cursor) {
        super(context, R.layout.row, cursor, from, to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindView(View row, Context context, Cursor cursor) {
        super.bindView(row, context, cursor);

        long createdAt = cursor.getLong(cursor.getColumnIndex(TimeLineDbHelper.C_CREATED_AT));

        TextView textCreatedAt = (TextView) row.findViewById(R.id.textCreatedAt);

        textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(createdAt));
    }

}