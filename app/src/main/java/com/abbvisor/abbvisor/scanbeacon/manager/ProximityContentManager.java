package com.abbvisor.abbvisor.scanbeacon.manager;

import android.content.Context;

import com.abbvisor.abbvisor.scanbeacon.data.BeaconID;

public class ProximityContentManager {

    private NearestBeaconManager nearestBeaconManager;

    private Listener listener;

    public ProximityContentManager(Context context) {

        nearestBeaconManager = new NearestBeaconManager(context);
        nearestBeaconManager.setListener(new NearestBeaconManager.Listener() {
            @Override
            public void onNearestBeaconChanged(BeaconID beaconID) {
                if (listener == null) {
                    return;
                }

                if (beaconID != null) {
                    listener.onContentChanged(beaconID);
                } else {
                    listener.onContentChanged(null);
                }
            }
        });

    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onContentChanged(BeaconID beaconID);
    }

    public void startContentUpdates() {
        nearestBeaconManager.startNearestBeaconUpdates();
    }

    public void stopContentUpdates() {
        nearestBeaconManager.stopNearestBeaconUpdates();
    }

    public void destroy() {
        nearestBeaconManager.destroy();
    }
}
