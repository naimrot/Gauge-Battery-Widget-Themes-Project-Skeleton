package net.hubalek.android.gaugebattwidget.themes.library;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import static net.hubalek.android.gaugebattwidget.themes.library.Constants.LOG_TAG;

/**
 * Parent for service.
 */
public abstract class AbstractStatusBarNotificationService extends Service {

    private static final int BATTERY_LEVEL_NOTIFICATION = 1;

    private Boolean startedForeground = Boolean.FALSE;

    private final IStatusBarInfoRemoteService.Stub mBinder = new IStatusBarInfoRemoteService.Stub() {

        public void updateStatusBarInfo(String title, String text, int level, String callbackPackageName, String callbackActivityName) {

            // get data to be displayed
            CharSequence tickerText = null;
            CharSequence contentTitle = title;
            CharSequence contentText = text;

            Log.d(LOG_TAG, "Callback activity: " + callbackPackageName + "/" + callbackActivityName);

            // get instance of notification manager
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // get status icon
            int icon = getStatusBarIconsArray()[level];

            // assign now to the variable
            long when = System.currentTimeMillis();

            Notification notification = new Notification(icon, tickerText, when);

            Context context = getApplicationContext();

            Intent notificationIntent = new Intent();
            notificationIntent.setComponent(new ComponentName(callbackPackageName, callbackActivityName));

            Log.d(LOG_TAG, "Notification intent used is " + notificationIntent);

            PendingIntent contentIntent = PendingIntent.getActivity(getApplication(), 0, notificationIntent, 0);

            Log.d(LOG_TAG, "Pending intent is " + contentIntent);

            notification.icon = icon;
            notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
            notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

            synchronized (startedForeground) {
                if (!startedForeground) {
                    startForeground(BATTERY_LEVEL_NOTIFICATION, notification);
                    startedForeground = true;
                } else {
                    mNotificationManager.notify(BATTERY_LEVEL_NOTIFICATION, notification);
                }
            }
        }

        public void hideStatusBarInfo() {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
            mNotificationManager.cancel(BATTERY_LEVEL_NOTIFICATION);
            synchronized (startedForeground) {
                if (startedForeground) {
                    stopForeground(true);
                    startedForeground = false;
                } else {
                    mNotificationManager.cancel(BATTERY_LEVEL_NOTIFICATION);
                }
            }

        }
    };

    protected abstract int[] getStatusBarIconsArray();

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

}
