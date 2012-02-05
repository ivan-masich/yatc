package com.masich.yatc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.*;
import android.database.Cursor;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.masich.yatc.TwitterConnection;
import com.masich.yatc.R;
import com.masich.yatc.TimeLineAdapter;
import com.masich.yatc.UpdaterService;
import com.masich.yatc.db.TimeLineModel;
import twitter4j.*;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Main activity.
 *
 * @author Masich Ivan <john@masich.com>
 */
public class YATCActivity extends BaseActivity {
    private Cursor cursor;
    private BroadcastReceiver twitterStatusReceiver;
    private TwitterConnection twitterConnection;
    private boolean activityInFocus = true;
    private Activity activity;
    private final int DIALOG_LOGOUT = 1;


    class TwitterStatusReceiver extends BroadcastReceiver {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            cursor.requery();
            
            if (activityInFocus) {
                clearNotification();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.main);

        twitterConnection = new TwitterConnection(this);

        bindUpdateStatusButtonListener();

        initTimeLine();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        clearNotification();

        activityInFocus = true;

        if (!twitterConnection.hasConnectionData()) {
            stopService(new Intent(this, UpdaterService.class));

            startActivity(new Intent(this, LoginActivity.class));

            finish();
        } else if (!UpdaterService.isRunning) {
            startService(new Intent(this, UpdaterService.class));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();

        activityInFocus = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        
        switch (item.getItemId()) {
            case R.id.menuLogout:
                showDialog(DIALOG_LOGOUT);
                break;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(twitterStatusReceiver);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = super.onCreateDialog(id);
        if (dialog != null) {
            return dialog;
        }

        switch (id) {
            case DIALOG_LOGOUT:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                
                builder.setMessage(R.string.alertLogout);
                
                builder.setPositiveButton(
                    R.string.yes,
                    new DialogInterface.OnClickListener() {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            twitterConnection.clean();
                            stopService(new Intent(activity, UpdaterService.class));

                            startActivity(new Intent(activity, LoginActivity.class));
                            activity.finish();
                        }
                    }
                );
                
                builder.setNegativeButton(
                    R.string.no,
                    new DialogInterface.OnClickListener() {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    }
                );

                return builder.create();
            default:
                return null;
        }
    }

    /**
     * Clear status notification.
     */
    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(UpdaterService.Updater.NOTIFICATION_ID);
    }

    /**
     * Bind onClick listener to update status button.
     */
    private void bindUpdateStatusButtonListener() {
        Button tweet = (Button)findViewById(R.id.buttonUpdate);

        tweet.setOnClickListener(
            new View.OnClickListener() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onClick(View v) {
                    try {
                        Twitter twitter = twitterConnection.getTwitter();

                        EditText editor = (EditText) findViewById(R.id.textStatus);
                        Editable editable = editor.getText();
                        String message = editable.toString();

                        if (message.equals("")) {
                            Toast.makeText(activity, R.string.toastStatusEmpty, Toast.LENGTH_SHORT).show();
                        } else {
                            twitter.updateStatus(editable.toString());
                            editable.clear();

                            Toast.makeText(activity, R.string.toastTwitPosted, Toast.LENGTH_SHORT).show();
                        }
                    } catch (TwitterException e) {
                        startActivity(new Intent(activity, LoginActivity.class));
                    }
                }
            }
        );
    }

    /**
     * Init time line.
     */
    private void initTimeLine() {
        ListView listTimeLine = (ListView) findViewById(R.id.listTimeline);

        TimeLineModel model = new TimeLineModel(this);
        cursor = model.getAll();
        startManagingCursor(cursor);

        TimeLineAdapter adapter = new TimeLineAdapter(this, cursor);
        listTimeLine.setAdapter(adapter);

        twitterStatusReceiver = new TwitterStatusReceiver();
        registerReceiver(
            twitterStatusReceiver,
            new IntentFilter(UpdaterService.ACTION_NEW_TWITTER_STATUS)
        );
    }
}