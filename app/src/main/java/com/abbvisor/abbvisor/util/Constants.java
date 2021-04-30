package com.abbvisor.abbvisor.util;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by natavit on 1/16/2017 AD.
 */

public final class Constants {

    private Constants() {}

    public static final String PACKAGE_NAME = "com.abbvisor.abbvisor";
    public static final String BROADCAST_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";
    public static final String ACTIVITY_EXTRA = PACKAGE_NAME + ".ACTIVITY_EXTRA";
    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES";

    public static final String CATEGORY_LOCATION_SERVICES = PACKAGE_NAME + ".CATEGORY_LOCATION_SERVICES";
    public static final String ACTION_GEOFENCE_TRANSITION = PACKAGE_NAME + ".ACTION_GEOFENCE_TRANSITION";

    /**
     * API URL
     */
    public static final String API_URL_NEARBY = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public static final String API_URL_DISTANCE_MATRIX = "https://maps.googleapis.com/maps/api/distancematrix/json?";

//    public static final String API_URL_ABBVISOR_BASE = "https://abbvisor.azurewebsites.net/";
//    public static final String API_URL_ABBVISOR_BASE = "https://abbvisor-demo.azurewebsites.net/";
    public static final String API_URL_ABBVISOR_BASE = "https://abbvisor.natavit.me/";
    public static final String API_URL_ABBVISOR_LOGIN = API_URL_ABBVISOR_BASE + "signin?";
    public static final String API_URL_ABBVISOR_BEACON = API_URL_ABBVISOR_BASE + "beaconContent?";

    /**
     * Maps
     */
    public static final int NEARBY_RADIUS = 5000;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 5;
    public static final LatLng mDefaultLocation = new LatLng(13.7469552, 100.536554); // Siam
    public static final int DEFAULT_ZOOM = 15;
    public static final int LOCATION_ZOOM = 16;

    /**
     * Geofence
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 250;
    public static final HashMap<String, LatLng> GEOFENCE_MARKS = new HashMap<>();
    public static final HashMap<String, Float> GEOFENCE_RADIUS = new HashMap<>();
    static {
        GEOFENCE_MARKS.put("Mahidol University", new LatLng(13.795229, 100.321867));
        GEOFENCE_RADIUS.put("Mahidol University", 800f);

        GEOFENCE_MARKS.put("Novotel Bangkok Sukhumvit 20", new LatLng(13.7315568, 100.561833));
        GEOFENCE_RADIUS.put("Novotel Bangkok Sukhumvit 20", 200f);

        GEOFENCE_MARKS.put("Home", new LatLng(13.774637, 100.486694));
        GEOFENCE_RADIUS.put("Home", 80f);

        GEOFENCE_MARKS.put("Central Plaza Pinklao", new LatLng(13.777940, 100.476151));
        GEOFENCE_RADIUS.put("Central Plaza Pinklao", 100f);
    }
    public static final int GEOFENCE_NOTIFICATION_ID = 23971;
    public static final int FCM_NOTIFICATION_ID = 23972;

    /**
     * SharedPreference Key
     */
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    public static final String GRAPH_REQUEST = PACKAGE_NAME + ".GRAPH_REQUEST";
    public static final String IS_FROM_NOTIFICATION = PACKAGE_NAME + ".IS_FROM_NOTIFICATION";
    public static final String PREFERENCE_LIST = PACKAGE_NAME + ".PREFERENCE_LIST";
}
