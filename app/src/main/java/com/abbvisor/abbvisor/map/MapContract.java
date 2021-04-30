package com.abbvisor.abbvisor.map;

import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by natavit on 2/12/2017 AD.
 */

public interface MapContract {

    interface View {
        void showAchievementActivity();
        void showSearchActivity();
        void showBeaconScannerActivity();
        void showSettingsActivity();
        void showSigninActivity();
        void showMyLocationButton(boolean show);
        void showNavigationButton(boolean show);
        void showGoogleMaps(Intent mapIntent);
        void moveCamera();
        void updateDestinationText();
        void addDirectionPath(LatLng currentLatLng, LatLng destLatLng);
        void removeDirectionPath();
        void showPreferencesActivity();
    }

    interface UserActionsListener {
        void searchDirection();
        void scanBeacon();
        void moveCameraToMyPosition();
        void navigate(String lat, String lng);
        void expandBottomSheet(BottomSheetBehavior behavior);
        void collapseBottomSheet(BottomSheetBehavior behavior);
        void editCharacteristics();
        void signout();
    }
}
