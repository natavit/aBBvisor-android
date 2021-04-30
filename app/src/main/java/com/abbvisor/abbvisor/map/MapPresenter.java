package com.abbvisor.abbvisor.map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.BottomSheetBehavior;

/**
 * Created by natavit on 2/12/2017 AD.
 */

public class MapPresenter implements MapContract.UserActionsListener {

    private final Context mContext;

    private final MapContract.View mView;

    public MapPresenter(Context context, MapContract.View mapsView) {
        mContext = context;
        mView = mapsView;
    }

    @Override
    public void searchDirection() {
        mView.showSearchActivity();
    }

    @Override
    public void scanBeacon() {
        mView.showBeaconScannerActivity();
    }

    @Override
    public void moveCameraToMyPosition() {
        mView.moveCamera();
        mView.showMyLocationButton(false);
    }

    @Override
    public void navigate(String lat, String lng) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mView.showGoogleMaps(mapIntent);
    }

    @Override
    public void expandBottomSheet(BottomSheetBehavior behavior) {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void collapseBottomSheet(BottomSheetBehavior behavior) {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void editCharacteristics() {
        mView.showPreferencesActivity();
    }

    @Override
    public void signout() {
        mView.showSigninActivity();
    }
}
