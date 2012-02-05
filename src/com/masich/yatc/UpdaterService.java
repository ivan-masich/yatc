package com.masich.yatc;

import java.util.List;

import com.masich.yatc.activity.YATCActivity;
import com.masich.yatc.db.TimeLineModel;
import twitter4j.Twitter;
import twitter4j.Status;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import twitter4j.TwitterException;

/**
 * Service for update twitter time line.
 *
 * @author Masich Ivan <john@masich.com>
 */
public class UpdaterService extends Service {
    public static boolean isRunning = false;
    public static final String LOG_TAG = "UpdaterService";
    public static final String ACTION_NEW_TWITTER_STATUS = "ACTION_NEW_TWITTER_STATUS";
    private Handler handler;
    private Updater updater;
    private TimeLineModel model;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler();

        model = new TimeLineModel(this);

        Log.d(LOG_TAG, "onCreate'd");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart(Intent i, int startId) {
        super.onStart(i, startId);

        updater = new Updater();
        handler.post(updater);

        isRunning = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(updater);

        isRunning = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class Updater implements Runnable {
        public static final int NOTIFICATION_ID = 47;
        static final long DELAY = 30000L;
        Notification notification;
        NotificationManager notificationManager;
        PendingIntent pendingIntent;

        Updater() {
            notificationManager = (NotificationManager) UpdaterService.this.getSystemService(Context.NOTIFICATION_SERVICE);

            notification = new Notification(
                android.R.drawable.stat_notify_sync,
                getString(R.string.app_name),
                System.currentTimeMillis()
            );
            pendingIntent = PendingIntent.getActivity(UpdaterService.this, 0, new Intent(UpdaterService.this, YATCActivity.class), 0);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            Twitter twitter = getTwitter();

            if (twitter != null) {
                boolean haveNewStatus = false;
                Log.d(UpdaterService.LOG_TAG, "Updater ran.");

                try {
                    List<Status> timeLine = twitter.getHomeTimeline();

                    for (Status status : timeLine) {
                        if (!model.checkExist(status)) {
                            model.insert(status);

                            haveNewStatus = true;
                        }
                    }
                } catch (TwitterException e) {
                    Log.e(LOG_TAG, "Updater.run exception: ", e);
                }

                if (haveNewStatus) {
                    sendBroadcast(new Intent(ACTION_NEW_TWITTER_STATUS));

                    haveNewStatusNotification();
                }
            }

            handler.postDelayed(this, DELAY);
        }

        /**
         * Send new status notification.
         */
        private void haveNewStatusNotification() {
            notification.setLatestEventInfo(
                    UpdaterService.this,
                    "New Twitter Status", "You have new tweets in the timeline",
                    pendingIntent
            );
            notification.when = System.currentTimeMillis();

            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private Twitter getTwitter() {
        TwitterConnection storage = new TwitterConnection(this);

        return storage.getTwitter();
    }
}