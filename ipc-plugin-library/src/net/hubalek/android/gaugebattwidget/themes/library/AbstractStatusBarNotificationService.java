package net.hubalek.android.gaugebattwidget.themes.library;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import net.hubalek.android.gaugebattwidget.service.IPluginCallbackService;

import static net.hubalek.android.gaugebattwidget.themes.library.Constants.LOG_TAG;

/**
 * Parent for service.
 */
public abstract class AbstractStatusBarNotificationService extends Service {

    private static final int BATTERY_LEVEL_NOTIFICATION = 1;

    private Boolean startedForeground = Boolean.FALSE;

    private final IStatusBarInfoRemoteService.Stub mBinder = new IStatusBarInfoRemoteService.Stub() {
        
        String lastDisplayedTitle = "";
        String lastDisplayedText = "";
        String lastCallbackPackageName = "";
        String lastCallbackActivityName = "";
        String lastCallbackServiceName = "";
        int lastLevel = -1;

        public synchronized void updateStatusBarInfo(String title, String text, int level, String callbackPackageName, String callbackActivityName, String callbackServiceName) {
            
            if (  !lastDisplayedTitle.equals(title) ||
                  !lastCallbackActivityName.equals(callbackActivityName) || 
                  !lastCallbackServiceName.equals(callbackServiceName) ||
                  !lastDisplayedText.equals(text) ||
                  lastLevel != level  
            ) {
                // get data to be displayed
                CharSequence tickerText = null;
                CharSequence contentTitle = title;
                CharSequence contentText = text;
                
                packageName = callbackPackageName;
                serviceName = callbackServiceName;
    
//                Log.d(LOG_TAG, "Callback activity: " + callbackPackageName + "/" + callbackActivityName);
    
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
    
//                Log.d(LOG_TAG, "Notification intent used is " + notificationIntent);
    
                PendingIntent contentIntent = PendingIntent.getActivity(getApplication(), 0, notificationIntent, 0);
    
//                Log.d(LOG_TAG, "Pending intent is " + contentIntent);
    
                notification.icon = icon;
                notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
                notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
    
                synchronized (startedForeground) {
                    if (!startedForeground) {
                        Log.d(LOG_TAG, "Starting service in foreground");
                        startForeground(BATTERY_LEVEL_NOTIFICATION, notification);
                        startedForeground = true;
                    } else {
                        Log.d(LOG_TAG, "Updating notification only...");
                        mNotificationManager.notify(BATTERY_LEVEL_NOTIFICATION, notification);
                    }
                }

                lastDisplayedTitle = title;
                lastDisplayedText = text;
                lastCallbackActivityName = callbackActivityName;
                lastCallbackPackageName = callbackPackageName;
                lastCallbackServiceName = callbackServiceName;
            } else {
                Log.w(LOG_TAG, "Update skipped.");
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
        Log.d(LOG_TAG, "onBind called...");
        registerBatteryReceiver();
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand called...");
        super.onStartCommand(intent, flags, startId);
        registerBatteryReceiver();
        Log.d(LOG_TAG, "receiverRegistered...");
        return START_STICKY;
    }

    private void registerBatteryReceiver() {
        mBatInfoReceiver = new BatteryBroadcastReceiver();
        registerReceiver(mBatInfoReceiver,  new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onDestroy() {
        if (mBatInfoReceiver != null) {
            unregisterReceiver(mBatInfoReceiver);
            mBatInfoReceiver = null;
        }
        super.onDestroy();    
    }


    private interface BoundServiceCallback {
        void onServiceBound(IPluginCallbackService iStatusBarInfoRemoteService);
    }
    
    private PluginCallbackServiceConnection mPluginCallbackServiceConnection = new PluginCallbackServiceConnection();
    
    private static class PluginCallbackServiceConnection implements ServiceConnection {

        public IPluginCallbackService mRemoteService;
        public BoundServiceCallback boundServiceCallback;
        public boolean connected = false;

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(Constants.LOG_TAG, "onServiceDisconnected(" + name + ")");
            connected = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(Constants.LOG_TAG, "onServiceConnected(" + name + ")");
            mRemoteService = IPluginCallbackService.Stub.asInterface(service);
            if (boundServiceCallback != null) {
                boundServiceCallback.onServiceBound(mRemoteService);
            }
            connected = true;
        }
    }

    private String packageName;
    private String serviceName;

    private BroadcastReceiver mBatInfoReceiver = null;

    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {

            final int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            final int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            final int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
            
            Log.d(LOG_TAG, "Battery changed: " + level + ", " + voltage + "mV, " + temperature + "°C, " + status);

            if (!mPluginCallbackServiceConnection.connected) {
                if (packageName != null && serviceName != null) {
                    mPluginCallbackServiceConnection.boundServiceCallback = new BoundServiceCallback() {
                        @Override
                        public void onServiceBound(IPluginCallbackService iStatusBarInfoRemoteService) {
                            try {
                                Log.d(LOG_TAG, "Updating info in parent widget after bind...");
                                iStatusBarInfoRemoteService.updateBatteryInfo(level, voltage, temperature, status);
                            } catch (RemoteException e) {
                                Log.w(LOG_TAG, "Error updating battery info: ", e);
                            }
                        }
                    };
                    Intent bindIntent = new Intent();
                    bindIntent.setClassName(packageName, serviceName);
                    Log.d(LOG_TAG, "Binding " + packageName + "/" + serviceName + " ...");
                    bindService(bindIntent, mPluginCallbackServiceConnection, Service.BIND_AUTO_CREATE);
                } else {
                    Log.w(LOG_TAG, "Bind skipped as packageName/serviceName is not known");
                }
            } else {
                try {
                    Log.d(LOG_TAG, "Updating info in parent widget, no bind needed...");
                    mPluginCallbackServiceConnection.mRemoteService.updateBatteryInfo(level, voltage, temperature, status);
                } catch (RemoteException e) {
                    Log.w(LOG_TAG, "Error updating battery info: ",e);
                } catch (NullPointerException e) {
                    Log.w(LOG_TAG, "mRemoteService==null?",e);
                }
            }
        }
    }
}
