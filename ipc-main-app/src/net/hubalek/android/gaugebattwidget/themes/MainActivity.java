package net.hubalek.android.gaugebattwidget.themes;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.example.mainapp.R;
import net.hubalek.android.gaugebattwidget.themes.library.IStatusBarInfoRemoteService;
import net.hubalek.android.gaugebattwidget.utils.IPCConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity. Used for testing of themes.
 */
public class MainActivity extends Activity {

    /**
     * You have to declare this in intent filter of main activity in your APK.
     */
    public static final String THEMES_INTENT_FILTER = IPCConstants.ACTION_SHOW_BATTERY_INFO;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerPluginsList);

        final Button button50 = (Button) findViewById(R.id.buttonShow50);
        final Button button25 = (Button) findViewById(R.id.buttonShow25);
        final Button button75 = (Button) findViewById(R.id.buttonShow75);
        final Button buttonClear = (Button) findViewById(R.id.buttonClear);

        Button buttonInfo = (Button) findViewById(R.id.buttonInfo);

        final List<ThemeInfo> activities= getThemesList(this);
        if (activities.isEmpty()) {
            spinner.setEnabled(false);
            button25.setEnabled(false);
            button50.setEnabled(false);
            button75.setEnabled(false);
            buttonClear.setEnabled(false);
            buttonInfo.setEnabled(false);
        } else {


            button50.setEnabled(true);
            buttonClear.setEnabled(true);
            buttonInfo.setEnabled(true);

            ArrayAdapter<ThemeInfo> adapter =
                    new ArrayAdapter<ThemeInfo>(
                            this,
                            android.R.layout.simple_spinner_item);

            for (ThemeInfo themeInfo : activities) {
                adapter.add(themeInfo);
            }


            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                
                ThemeInfo previousTheme = null;
                
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    
                    if (previousTheme != null) {
                        clearThemeInfo(previousTheme);
                    }
                    
                    final ThemeInfo themeInfo = (ThemeInfo) adapterView.getItemAtPosition(i);
                    Log.d(GlobalLogTag.TAG, "Selected item #" + i + ", " + themeInfo);

                    Intent serviceIntent=new Intent();
                    serviceIntent.setClassName(themeInfo.packageName, themeInfo.serviceName);
                    boolean ok = bindService(serviceIntent, mServiceConnection,Context.BIND_AUTO_CREATE);
                    
                    Log.d(GlobalLogTag.TAG, "bind succesfull " + ok);

                    previousTheme = themeInfo;

                    button50.setOnClickListener(new ShowPercentListener(themeInfo, 50));
                    button25.setOnClickListener(new ShowPercentListener(themeInfo, 25));
                    button75.setOnClickListener(new ShowPercentListener(themeInfo, 75));

                    buttonClear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clearThemeInfo(themeInfo);
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

    }

    /**
     * Clears status bar notification.
     * @param themeInfo
     */
    private void clearThemeInfo(ThemeInfo themeInfo) {
        mServiceConnection.clearInfo();
    }

    /**
     * Finds all themes installed.
     *
     * @param activity context used for query
     * @return list of theme infos.
     */
    public static List<ThemeInfo> getThemesList(Activity activity) {
        List<ThemeInfo> retVal = new ArrayList<ThemeInfo>();
        Intent levelsIntent = new Intent(THEMES_INTENT_FILTER);
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(levelsIntent, 0);
        for (ResolveInfo resolveInfo : activities) {
            Log.d(GlobalLogTag.TAG, "Found " + resolveInfo);

            PackageInfo packageInfo = null;
            try {
                packageInfo = packageManager.getPackageInfo(resolveInfo.activityInfo.packageName, PackageManager.GET_SERVICES);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(GlobalLogTag.TAG, "Error getting info about package " + resolveInfo.activityInfo.packageName);
            }
            if (packageInfo != null && packageInfo.services != null && packageInfo.services.length > 0) {
                ThemeInfo themeInfo = new ThemeInfo();
                themeInfo.serviceName = packageInfo.services[0].name;
                themeInfo.packageName = resolveInfo.activityInfo.packageName;
                retVal.add(themeInfo);
            }

        }
        return retVal;
    }

    /**
     * Holds instance of the connection.
     */
    private RemoteNotificationServiceConnection mServiceConnection = new RemoteNotificationServiceConnection();

    /**
     * Connection to remote service.
     */
    private static class RemoteNotificationServiceConnection implements ServiceConnection {

        /**
         * Holds connection into remote service
         */
        public IStatusBarInfoRemoteService mRemoteService;

        /**
         * Called when service disconnected.
         * @param name name of the service
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // does nothing
        }

        /**
         * Called when service connected.
         *
         * @param name name of the service
         * @param service binder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // get instance of the aidl binder
            mRemoteService = IStatusBarInfoRemoteService.Stub.asInterface(service);
        }

        /**
         * Displays status bar notification.
         *
         * @param title title of the notification
         * @param text text of the notification
         * @param level battery level
         * @param callbackPackageName package name of the activity that should be called when status bar notification clicked
         * @param callbackActivityName name of the activity that should be called when status bar notification clicked
         */
        public void displayInfo(String title, String text, int level, String callbackPackageName, String callbackActivityName) {
            try {
                mRemoteService.updateStatusBarInfo(title, text, level, callbackPackageName, callbackActivityName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /**
         * Calls remote method for clearing of status bar notification.
         */
        public void clearInfo() {
            try {
                mRemoteService.hideStatusBarInfo();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Listener showing percent info.
     */
    private class ShowPercentListener implements View.OnClickListener {
        /**
         * Theme info.
         */
        private final ThemeInfo themeInfo;

        /**
         * Percent to be shown.
         */
        private final int percent;

        /**
         * Class constructor.
         * @param themeInfo theme info
         * @param percent percent to be shown.
         */
        public ShowPercentListener(ThemeInfo themeInfo, int percent) {
            this.themeInfo = themeInfo;
            this.percent = percent;
        }

        /**
         * Called when clicked on {@View}
         * @param view
         */
        @Override
        public void onClick(View view) {
            Intent serviceIntent = new Intent();
            serviceIntent.setClassName(themeInfo.packageName, themeInfo.serviceName);
            bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            mServiceConnection.displayInfo("Title","Text",percent, getPackageName(), CallbackActivity.class.getName().toString());
        }
    }


}
