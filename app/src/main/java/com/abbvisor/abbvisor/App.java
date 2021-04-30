package com.abbvisor.abbvisor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import com.abbvisor.abbvisor.scanbeacon.BeaconScannerActivity;
import com.abbvisor.abbvisor.util.Constants;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.facebook.appevents.AppEventsLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static com.abbvisor.abbvisor.util.LogUtils.LOGE;

/**
 * Created by natavit on 1/10/2017 AD.
 */

public class App extends MultiDexApplication {

    private static final String TAG = App.class.getSimpleName();

    private BeaconManager beaconManager;


    @Override
    public void onCreate() {
        super.onCreate();

//        SystemClock.sleep(TimeUnit.SECONDS.toMillis(3)); // For capture splash screen
        overrideDefaultFont();
        AppEventsLogger.activateApp(this);

        //initBeaconManager();
    }

    private void initBeaconManager() {
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                LOGE(TAG, "Ready");
                beaconManager.startMonitoring(new Region(
                        "ABBVISOR_BEACONS",
                        UUID.fromString("FB8D8932-8EC7-8A79-33AE-0BEBB247255C"), null, null));
            }
        });
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                sendNotification("You have found beacon(s)", "Press to start scanning!");
                LOGE(TAG, "Enter");
            }

            @Override
            public void onExitedRegion(Region region) {
                removeNotification();
                LOGE(TAG, "Exit");
            }
        });

//        beaconManager.setBackgroundScanPeriod(10L, 1L);
    }

    private void sendNotification(String title, String message) {
        SharedPreferences sp = this.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean signedIn = sp.getBoolean("signed_in", false);
        boolean runningScanner = sp.getBoolean("running_scanner", false);
        LOGE(TAG, "signed_in: " + signedIn);
        if (signedIn && !runningScanner) {
            sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String ringtone = sp.getString(this.getString(R.string.pref_key_notifications_ringtone),
                    this.getString(R.string.pref_default_ringtone));

            Intent notifyIntent = new Intent(this, BeaconScannerActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                    new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                            R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSound(Uri.parse(ringtone))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

//            notification.defaults |= Notification.DEFAULT_SOUND;
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Constants.GEOFENCE_NOTIFICATION_ID, notification);
        }
    }

    private void removeNotification() {
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.GEOFENCE_NOTIFICATION_ID);
    }

    private void overrideDefaultFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Aller-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.abbvisor.abbvisor", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("App", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
