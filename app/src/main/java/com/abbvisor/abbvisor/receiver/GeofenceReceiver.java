package com.abbvisor.abbvisor.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.manager.AccountManager;
import com.abbvisor.abbvisor.scanbeacon.BeaconScannerActivity;
import com.abbvisor.abbvisor.util.Constants;
import com.abbvisor.abbvisor.util.GeofenceErrorMessages;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import static com.abbvisor.abbvisor.util.LogUtils.LOGE;

/**
 * Created by natavit on 1/28/2017 AD.
 */

public class GeofenceReceiver extends BroadcastReceiver {

    private static final String TAG = GeofenceReceiver.class.getSimpleName();

    private static final int REQ_CODE_GEOFENCE = 3333;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constants.ACTION_GEOFENCE_TRANSITION)) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

            if (geofencingEvent.hasError()) {
                handleError(context, geofencingEvent);
            } else {
                handleTransition(context, geofencingEvent);
            }
        }
    }

    private void handleTransition(Context context, GeofencingEvent geofencingEvent) {

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        String geofenceTransitionDetails = getGeofenceTransitionDetails(
                context,
                geofenceTransition,
                triggeringGeofences
        );

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            if (!AccountManager.getInstance(context).isOpenedFromNoti()) {
                sendNotification(context, geofenceTransitionDetails);
            }

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            removeNotification(context);

        } else {
            LOGE(TAG, context.getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    private void handleError(Context context, GeofencingEvent geofencingEvent) {
        String errorMessage = GeofenceErrorMessages.getErrorString(context,
                geofencingEvent.getErrorCode());
        LOGE(TAG, errorMessage);
    }

    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(context, geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private void sendNotification(Context context, String notificationDetails) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean send = sp.getBoolean(context.getString(R.string.pref_key_notifications_beacon_area), true);

        if (send) {
            String ringtone = sp.getString(context.getString(R.string.pref_key_notifications_ringtone),
                    context.getString(R.string.pref_default_ringtone));

            Intent notificationIntent = new Intent(context, BeaconScannerActivity.class);
            notificationIntent.putExtra(Constants.IS_FROM_NOTIFICATION, true);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivities(context, 0,
                    new Intent[]{notificationIntent}, PendingIntent.FLAG_UPDATE_CURRENT);

//            Intent notificationIntent = new Intent(context, BeaconScannerActivity.class);
//            notificationIntent.putExtra(Constants.IS_FROM_NOTIFICATION, true);
//
//            PendingIntent notificationPendingIntent =
//                    TaskStackBuilder.create(context)
//                    .addNextIntentWithParentStack(notificationIntent)
//                    .getPendingIntent(REQ_CODE_GEOFENCE, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.mipmap.ic_launcher))
                    .setContentTitle(notificationDetails)
                    .setContentText(context.getString(R.string.geofence_transition_notification_text))
                    .setContentIntent(pendingIntent)
                    .setSound(Uri.parse(ringtone))
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(Constants.GEOFENCE_NOTIFICATION_ID, notification);
        }
    }

    private void removeNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.GEOFENCE_NOTIFICATION_ID);
    }

    private String getTransitionString(Context context, int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return context.getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return context.getString(R.string.geofence_transition_exited);
            default:
                return context.getString(R.string.geofence_unknown_transition);
        }
    }
}
