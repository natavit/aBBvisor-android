package com.abbvisor.abbvisor.map;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.achievement.AchievementActivity;
import com.abbvisor.abbvisor.databinding.ActivityMapsBinding;
import com.abbvisor.abbvisor.manager.AccountManager;
import com.abbvisor.abbvisor.manager.HttpManager;
import com.abbvisor.abbvisor.manager.PlaceListManager;
import com.abbvisor.abbvisor.map.adapter.FavouritePlaceAdapter;
import com.abbvisor.abbvisor.map.adapter.NearbyPlaceAdapter;
import com.abbvisor.abbvisor.model.place.Geometry;
import com.abbvisor.abbvisor.model.place.PlaceList;
import com.abbvisor.abbvisor.model.place.Result;
import com.abbvisor.abbvisor.model.place.distancematrix.DistanceMatrix;
import com.abbvisor.abbvisor.scanbeacon.BeaconScannerActivity;
import com.abbvisor.abbvisor.selectcharacteristic.CharacteristicActivity;
import com.abbvisor.abbvisor.settings.SettingsActivity;
import com.abbvisor.abbvisor.signin.SigninActivity;
import com.abbvisor.abbvisor.util.Constants;
import com.abbvisor.abbvisor.util.DeviceUtils;
import com.abbvisor.abbvisor.util.GeofenceErrorMessages;
import com.abbvisor.abbvisor.util.animation.ResizeAnimation;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.abbvisor.abbvisor.R.id.map;
import static com.abbvisor.abbvisor.util.DeviceUtils.dpToPx;
import static com.abbvisor.abbvisor.util.LogUtils.LOGE;

public class MapActivity extends AppCompatActivity implements
        View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnCameraMoveStartedListener,
        ResultCallback<Status>,
        MapContract.View {

    private static final String TAG = MapActivity.class.getSimpleName();

    private static final String KEY_CAMERA_POSITION = "CAMERA_POSITION";
    private static final String KEY_LOCATION = "LOCATION";
    private static final String KEY_GEOFENCE_ADDED = "GEOFENCE_ADDED";
    private static final String KEY_CURRENT_LOCATION = "CURRENT_LOCATION";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1111;
    private static final int REQ_CODE_PLACE_AUTOCOMPLETE = 2222;
    private static final int REQ_CODE_GEOFENCE = 3333;
    private static final int REQ_CODE_LOCATION = 4444;
    private static final int REQ_CODE_PLACE_PICKER = 5555;

    private SharedPreferences mSharedPreferences;

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private boolean mLocationPermissionGranted;

    private ArrayList<Geofence> mGeofenceList;
    private boolean mGeofencesAdded;

    private Location mCurrentLocation;
    //    private Place mDestination;
    private Result mDestinationResult;
    private String mDuration;
    private Marker markerFrom;
    private Marker mMarkerTo;
    private Polyline mPolyline;

    private boolean mDirectionShown;
    private boolean mCameraLocked;

    private boolean mLocationOn;

    //    private NearbyPlacesLoadCallback mNearbyPlacesLoadCallback;
    private boolean mNearbyLoaded;
    private NearbyPlaceAdapter mNearbyPlaceAdapter;

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private BottomSheetBehavior mBottomSheetBehavior;

    private boolean mBottomSheetCollapsed;

    private PlaceListManager mPlaceListManager;
    private FavouritePlaceAdapter mFavouritePlaceAdapter;

    private MapContract.UserActionsListener mActionsListener;

    ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps);

        checkGooglePlayServices();

        checkUserStatus();
        initInstances(savedInstanceState);
        buildGoogleApiClient();

        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            mGeofencesAdded = savedInstanceState.getBoolean(KEY_GEOFENCE_ADDED);
            mPlaceListManager.onRestoreInstanceState(savedInstanceState.getBundle("placeListManager"));
        }
    }

    private void checkGooglePlayServices() {
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(MapActivity.this);
    }

    private void checkUserStatus() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }
    }

    private void initInstances(Bundle savedInstanceState) {

        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, SigninActivity.class));
            finish();
            return;
        }

        String socialProvider = user.getProviders().get(0);
        AccountManager.getInstance(this).setProfile(user, socialProvider);

        mPlaceListManager = new PlaceListManager(this);
        mActionsListener = new MapPresenter(this, this);

        initToolbar();
        initFavouritePlacesView();

        initBottomSheet();
        initNearbyPlacesView();

        binding.mapUi.searchBarInput.setOnClickListener(this);
        binding.mapUi.fabMyLocation.setOnClickListener(this);
        binding.mapUi.fabNavigation.setOnClickListener(this);
        binding.mapUi.fabScanBeacon.setOnClickListener(this);
        binding.mapUi.btnRetry.setOnClickListener(this);

        TextView profileName = (TextView) binding.navView.navigation
                .getHeaderView(0).findViewById(R.id.tv_profile_name);
        profileName.setText(AccountManager.getInstance(this).getName());

        if (mPlaceListManager.getCount() == 0) {
            binding.navView.tvFavPlace.setVisibility(View.INVISIBLE);
        }

        binding.navView.btnSignOut.setCustomTextFont("Aller-Regular.ttf");
        binding.navView.btnSignOut.setOnClickListener(this);
        binding.navView.navigation.setItemIconTintList(null);
        binding.navView.navigation.setNavigationItemSelectedListener(this);

        mCameraLocked = true;
        mDirectionShown = false;
        mNearbyLoaded = false;

        mBottomSheetCollapsed = true;

        mGeofenceList = new ArrayList<>();
        mGeofencesAdded = false;
    }

    private void initBottomSheet() {
        binding.mapUi.bottomSheet.setOnClickListener(this);
        mBottomSheetBehavior = BottomSheetBehavior.from(binding.mapUi.bottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetCollapsed = true;
                    binding.mapUi.ivNearbyPlace.setImageDrawable(
                            ContextCompat.getDrawable(MapActivity.this, R.drawable.ic_keyboard_arrow_up_white_24dp));
                } else if (newState == BottomSheetBehavior.STATE_DRAGGING) {

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetCollapsed = false;
                    binding.mapUi.ivNearbyPlace.setImageDrawable(
                            ContextCompat.getDrawable(MapActivity.this, R.drawable.ic_keyboard_arrow_down_white_24dp));
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Bottom Sheet Margin
                int m1 = dpToPx(MapActivity.this, (int) (16 - (slideOffset * 16)));
                ResizeAnimation anim = new ResizeAnimation(
                        binding.mapUi.bottomSheet,
                        mBottomSheetBehavior,
                        m1
                );
                anim.setDuration(0);
                binding.mapUi.bottomSheet.startAnimation(anim);

                if (slideOffset == 1)
                    binding.mapUi.fabMyLocation.setVisibility(View.GONE);
                else if (!mCameraLocked && slideOffset <= 0.4)
                    binding.mapUi.fabMyLocation.setVisibility(View.VISIBLE);

                // Fab My Location Scale
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(
                        binding.mapUi.fabMyLocation,
                        View.SCALE_X,
                        1 - slideOffset
                );
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(
                        binding.mapUi.fabMyLocation,
                        View.SCALE_Y,
                        1 - slideOffset
                );
                AnimatorSet animSet = new AnimatorSet();
                animSet.playTogether(scaleX, scaleY);
                animSet.setDuration(0);
                animSet.start();
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(binding.mapUi.toolbar);

        mActionBarDrawerToggle
                = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.mapUi.toolbar,
                R.string.action_open_drawer,
                R.string.action_close_drawer
        );

        binding.drawerLayout.addDrawerListener(mActionBarDrawerToggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initFavouritePlacesView() {
        mFavouritePlaceAdapter = new FavouritePlaceAdapter(this, mItemListener, mPlaceListManager);
        mFavouritePlaceAdapter.setFavouritePlaces(mPlaceListManager.getFavouritePlaces());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.navView.rvFavPlace.setLayoutManager(linearLayoutManager);

        SnapHelper snapHelperTop = new GravitySnapHelper(Gravity.TOP);
        snapHelperTop.attachToRecyclerView(binding.navView.rvFavPlace);

        binding.navView.rvFavPlace.setAdapter(mFavouritePlaceAdapter);
    }

    private void initNearbyPlacesView() {
        mNearbyPlaceAdapter = new NearbyPlaceAdapter(this, mItemListener, mPlaceListManager, mFavouritePlaceAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.mapUi.rvNearbyPlace.setLayoutManager(linearLayoutManager);

        SnapHelper snapHelperTop = new GravitySnapHelper(Gravity.TOP);
        snapHelperTop.attachToRecyclerView(binding.mapUi.rvNearbyPlace);

        binding.mapUi.rvNearbyPlace.setAdapter(mNearbyPlaceAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LOGE(TAG, "onResume");

        checkGooglePlayServices();

//        if (!mLocationOn) {
//            checkLocationSettings();
//        }

        if (mGoogleApiClient.isConnected()) {
            getDeviceLocation();
        }

        if (mCurrentLocation != null && !mNearbyLoaded) {
            LOGE(TAG, "Load");
            loadNearbyPlaces();
        }
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        if (mMap != null && mMap.getCameraPosition() != null)
            mCameraPosition = mMap.getCameraPosition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AccountManager.getInstance(this).setOpenedFromNoti(true);

        mNearbyLoaded = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mCurrentLocation);
            outState.putBoolean(KEY_GEOFENCE_ADDED, mGeofencesAdded);
        }

        outState.putBundle("placeListManager",
                mPlaceListManager.onSaveInstanceState());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_PLACE_AUTOCOMPLETE: {
                if (resultCode == RESULT_OK) {
                    if (!DeviceUtils.isInternetConnectionAvailable(MapActivity.this)) {
                        Toast.makeText(MapActivity.this, "No Cnternet Connection...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Place destination = PlaceAutocomplete.getPlace(this, data);
                    mDestinationResult = saveToResult(destination);
                    requestDistanceMatrix(mDestinationResult);
                    updateDestinationText();
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;
            }
            case REQ_CODE_LOCATION: {
                if (resultCode == RESULT_OK) {
                    mLocationOn = true;
                    Toast.makeText(MapActivity.this, "Location enabled", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            case REQ_CODE_PLACE_PICKER: {
                if (resultCode == RESULT_OK) {
                    if (!DeviceUtils.isInternetConnectionAvailable(MapActivity.this)) {
                        Toast.makeText(MapActivity.this, "No Cnternet Connection...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Place destination = PlaceAutocomplete.getPlace(this, data);
                    mDestinationResult = saveToResult(destination);
                    requestDistanceMatrix(mDestinationResult);
                    updateDestinationText();
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    showMyLocationButton(true);
                }
                break;
            }
        }
        updateLocationUI();
    }

    private synchronized void buildGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestId()
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        createLocationRequest();
    }

    @Override
    public void updateDestinationText() {
        String name = mDestinationResult.getName();

        if (mDirectionShown)
            removeDirectionPath();

        if (name.length() > 0) {
            binding.mapUi.searchBarInput.setText(name.length() > 0 ? name : "");
            requestDirectionPath();
        }

//        if (!mCameraLocked)
        showMyLocationButton(true);
        showNavigationButton(true);
    }

    private void requestDirectionPath() {
        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        LatLng destLatLng = mDestinationResult.getLatLng();

        GoogleDirection.withServerKey(getString(R.string.google_api_key))
                .from(currentLatLng)
                .to(destLatLng)
                .transportMode(TransportMode.DRIVING)
                .unit(Unit.METRIC)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        String status = direction.getStatus();
                        if (status.equals(RequestResult.OK)) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(
                                    MapActivity.this, directionPositionList, 5, Color.RED
                            );

                            mPolyline = mMap.addPolyline(polylineOptions);

                            mDuration = leg.getDuration().getText();
                        } else if (status.equals(RequestResult.ZERO_RESULTS)) {
                            Toast.makeText(MapActivity.this,
                                    "There is no driving route available for this destination",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                    }
                });

        addDirectionPath(currentLatLng, destLatLng);
    }

    @Override
    public void addDirectionPath(LatLng currentLatLng, LatLng destLatLng) {
        mDirectionShown = true;

        MarkerOptions marker = new MarkerOptions()
                .position(destLatLng)
                .title(mDestinationResult.getName())
                .snippet(mDestinationResult.getAddress().toString());

        mMarkerTo = mMap.addMarker(marker);

        LatLngBounds bounds = LatLngBounds.builder()
                .include(currentLatLng)
                .include(destLatLng)
                .build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        mMap.animateCamera(cu);
    }

    @Override
    public void removeDirectionPath() {
        binding.mapUi.searchBarInput.setText("");
        mDirectionShown = false;

        if (mPolyline != null)
            mPolyline.remove();

        if (mMarkerTo != null)
            mMarkerTo.remove();

//        markerFrom.remove();
//        mDestinationResult = null;
        showNavigationButton(false);
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        checkLocationSettings();
    }

    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setNeedBle(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
//                final LocationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        mLocationOn = true;
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MapActivity.this, REQ_CODE_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        mLocationOn = false;
                        break;
                }
            }
        });
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    private void updateMarkers() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
//            loadNearbyPlaces();

        } else {
//            mMap.addMarker(new MarkerOptions()
//                    .position(mDefaultLocation)
//                    .title(getString(R.string.default_info_title))
//                    .snippet(getString(R.string.default_info_snippet)));
        }
    }

    private void loadNearbyPlaces() {
        mNearbyLoaded = true;
        String location = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();

        Observable<PlaceList> observable
                = HttpManager.getInstance().getService().loadNearbyPlaceList(
                Constants.API_URL_NEARBY,
                getString(R.string.google_api_key),
                location,
                Constants.NEARBY_RADIUS
        );

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PlaceList>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final PlaceList placeList) {

//                        LOGE(TAG, "Size: " + placeList.getResults().size());

                        for (final Result res : placeList.getResults()) {
                            Places.GeoDataApi.getPlaceById(mGoogleApiClient, res.getPlaceId())
                                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                                                           @Override
                                                           public void onResult(@NonNull PlaceBuffer places) {
                                                               if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                                                   Place frozen = places.get(0).freeze();
                                                                   Result result = saveToResult(frozen);
                                                                   requestDistanceMatrix(result);
                                                               } else {
                                                                   LOGE(TAG, "Place not found");
                                                               }
                                                               places.release();
                                                           }
                                                       }
                                    );
                        }

//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                loadNextNearbyPlaces(placeList);
//                            }
//                        }, 2500);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOGE(TAG, "Nearby: " + e.getLocalizedMessage());
                        binding.mapUi.progressBar.setVisibility(View.GONE);
                        binding.mapUi.btnRetry.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onComplete() {
                        binding.mapUi.progressBar.setVisibility(View.GONE);
                        binding.mapUi.btnRetry.setVisibility(View.GONE);
                    }
                });
    }

    private void loadNextNearbyPlaces(final PlaceList placeList) {
        Observable<PlaceList> observable
                = HttpManager.getInstance().getService().loadNextNearbyPlaceList(
                Constants.API_URL_NEARBY,
                getString(R.string.google_api_key),
                placeList.getNextPageToken()
        );

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PlaceList>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PlaceList _placeList) {
                        for (Result res : placeList.getResults()) {
                            Places.GeoDataApi.getPlaceById(mGoogleApiClient, res.getPlaceId())
                                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                                                           @Override
                                                           public void onResult(@NonNull PlaceBuffer places) {
                                                               if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                                                   Place frozen = places.get(0).freeze();
                                                                   Result result = saveToResult(frozen);
                                                                   requestDistanceMatrix(result);
                                                               } else {
                                                                   LOGE(TAG, "Place not found");
                                                               }
                                                               places.release();
                                                           }
                                                       }
                                    );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOGE(TAG, "onError (2)");
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private Result saveToResult(Place place) {
        Result result = new Result();
        result.setPlaceId(place.getId());
        result.setGeometry(
                new Geometry(new com.abbvisor.abbvisor.model.place.Location(
                        place.getLatLng().latitude,
                        place.getLatLng().longitude)
                ));
        result.setName(place.getName().toString());
        result.setAddress(place.getAddress().toString());
        result.setPhoneNumber(place.getPhoneNumber().toString());
        result.setPlaceTypes(place.getPlaceTypes());
        result.setRating(place.getRating());

        return result;
    }


    private void requestDistanceMatrix(final Result des) {
        // Enable distance matrix
        boolean load = true;
        if (load) {
            String origin = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();
            String destination = des.getLatLng().latitude + "," + des.getLatLng().longitude;

            Observable<DistanceMatrix> observable
                    = HttpManager.getInstance().getService().findDistanceMatrix(
                    Constants.API_URL_DISTANCE_MATRIX,
                    getString(R.string.google_api_key),
                    origin,
                    destination
            );

            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<DistanceMatrix>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(DistanceMatrix value) {
                            String status = value.getStatus();
                            if (status.equals("OK")) {
                                mNearbyPlaceAdapter.appendNearbyPlacesAtBottomPosition(
                                        des, value.getRows().get(0).getElements().get(0));
                            } else if (status.equals("OVER_QUERY_LIMIT")) {
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            LOGE(TAG, "onError " + e.getLocalizedMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    // TODO: Create service for adding geofence
    private void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.GEOFENCE_MARKS.entrySet()) {

            String key = entry.getKey();
            LatLng latLng = entry.getValue();
            float radius = Constants.GEOFENCE_RADIUS.get(key);

            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(key)
                    .setCircularRegion(
                            latLng.latitude,
                            latLng.longitude,
                            radius
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());

            List<PatternItem> pattern = Arrays.asList(new Dot(), new Gap(20));

            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .strokeColor(Color.argb(60, 14, 61, 56))
                    .strokeWidth(6f)
                    .strokePattern(pattern)
                    .fillColor(Color.argb(60, 14, 61, 56))
                    .radius(radius);

            mMap.addCircle(circleOptions);
        }

        addGeofences();
    }

    public void addGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(Constants.ACTION_GEOFENCE_TRANSITION);
//        LOGE(TAG, "getGeofencePendingIntent: " + Constants.ACTION_GEOFENCE_TRANSITION);
        return PendingIntent.getBroadcast(this, REQ_CODE_GEOFENCE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @SuppressWarnings("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null)
            return;

        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false); // True to show the button
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mCurrentLocation = null;
        }
    }

    @Override
    public void showMyLocationButton(boolean show) {
        if (show) {
            mCameraLocked = false;

//            if (!mBottomSheetCollapsed) return;

            AnimatorSet anim
                    = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.fab_in);
            anim.setTarget(binding.mapUi.fabMyLocation);
            anim.start();
            binding.mapUi.fabMyLocation.setVisibility(View.VISIBLE);

        } else {
            mCameraLocked = true;
            AnimatorSet anim
                    = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.fab_out);
            anim.setTarget(binding.mapUi.fabMyLocation);
            anim.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!mCameraLocked)
                        binding.mapUi.fabMyLocation.setVisibility(View.GONE);
                }
            }, 500);
        }
    }

    @Override
    public void showNavigationButton(boolean show) {
        if (show) {
            AnimatorSet anim
                    = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.fab_in);
            anim.setTarget(binding.mapUi.fabNavigation);
            anim.start();
            binding.mapUi.fabNavigation.setVisibility(View.VISIBLE);

        } else {
            AnimatorSet anim
                    = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.fab_out);
            anim.setTarget(binding.mapUi.fabNavigation);
            anim.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.mapUi.fabNavigation.setVisibility(View.GONE);
                }
            }, 500);
        }
    }

    @Override
    public void showGoogleMaps(Intent mapIntent) {
        startActivity(mapIntent);
    }

    @Override
    public void showSearchActivity() {
        // Google Place Autocomplete
        if (!mLocationOn) {
            checkLocationSettings();
        } else {
            try {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                startActivityForResult(builder.build(MapActivity.this), REQ_CODE_PLACE_PICKER);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
//            try {
////                LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//
////                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
////                        .setCountry("TH")
////                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
////                        .build();
//
//                Intent intent =
//                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
////                                .setFilter(typeFilter)
//                                .build(this);
//                startActivityForResult(intent, REQ_CODE_PLACE_AUTOCOMPLETE);
//            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
//                e.printStackTrace();
//            }
            // My custom search
//            Intent intent = new Intent(MapActivity.this, PlaceSearchActivity.class);
//            intent.putExtra("destination", mDestination);
//            startActivityForResult(intent, REQ_CODE_SEARCH);
        }
    }

    @Override
    public void showSigninActivity() {
        if (mCameraLocked)
            mCameraLocked = false;

        startActivity(new Intent(this, SigninActivity.class));
        AccountManager.getInstance(this).signOut(mGoogleApiClient);
        finish();
    }

    @Override
    public void showBeaconScannerActivity() {
        startActivity(new Intent(this, BeaconScannerActivity.class));
    }

    @Override
    public void showPreferencesActivity() {
        startActivity(new Intent(this, CharacteristicActivity.class));
    }

    @Override
    public void showAchievementActivity() {
        startActivity(new Intent(this, AchievementActivity.class));
    }

    @Override
    public void showSettingsActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    public void moveCamera() {
        if (mCurrentLocation == null) {
            Toast.makeText(MapActivity.this, "Please check your location service", Toast.LENGTH_SHORT).show();
        } else {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.LOCATION_ZOOM));

            showMyLocationButton(false);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        getDeviceLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;


        if (!mNearbyLoaded) {
            if (!mLocationOn) {
                checkLocationSettings();
            } else {
                loadNearbyPlaces();
                moveCamera();
            }
        }
    }


    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = true;

        } else {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnCameraMoveStartedListener(this);

        int p1 = DeviceUtils.dpToPx(this, 84);
        int p2 = DeviceUtils.dpToPx(this, 16);
        mMap.setPadding(p2, p1, p2, p1);

        updateLocationUI();
        updateMarkers();

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

                TextView title = (TextView) infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = (TextView) infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                TextView time = (TextView) infoWindow.findViewById(R.id.duration);
                time.setText(String.format(getString(R.string.text_duration), mDuration));

                return infoWindow;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
//                if (mPlaceListManager.isFavouritePlace(mDestinationResult)) {
//                    builder.setTitle("Unfavourite")
//                            .setMessage("Do you want to remove this place from your favourite list ?")
//                            .setCancelable(true)
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    mItemListener.onUnlikeClick(mDestinationResult);
//                                }
//                            })
//                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                }
//                            })
//                            .create();
//                } else {
//                    builder.setTitle("Favourite")
//                            .setMessage("Do you want to save this place to your favourite list ?")
//                            .setCancelable(true)
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    mItemListener.onLikeClick(mDestinationResult);
//                                }
//                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                }
//                            })
//                            .create();
//                }
//                builder.create().show();
            }
        });

        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mCurrentLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()), Constants.DEFAULT_ZOOM));
        } else {
            LOGE(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.mDefaultLocation, Constants.DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        if (!mGeofencesAdded)
            populateGeofenceList();
    }

    @Override
    public void onCameraMoveStarted(int i) {
        if (!mLocationOn) {
            checkLocationSettings();
            return;
        }

        if (i == REASON_GESTURE) {
            if (mCameraLocked) {
                showMyLocationButton(true);
            }
            if (!mBottomSheetCollapsed) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (mDirectionShown) {
            removeDirectionPath();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_bar_input:
                if (!DeviceUtils.isInternetConnectionAvailable(MapActivity.this)) {
                    Toast.makeText(MapActivity.this, "No Internet Connection...", Toast.LENGTH_SHORT).show();
                    return;
                }
                mActionsListener.searchDirection();
                break;
            case R.id.fab_my_location:
                mActionsListener.moveCameraToMyPosition();
                break;
            case R.id.fab_navigation:
                if (mDestinationResult != null && mDestinationResult.getLatLng() != null) {
                    String lat = String.valueOf(mDestinationResult.getLatLng().latitude);
                    String lng = String.valueOf(mDestinationResult.getLatLng().longitude);
                    mActionsListener.navigate(lat, lng);
                }
                break;
            case R.id.fab_scan_beacon:
                if (!DeviceUtils.isInternetConnectionAvailable(MapActivity.this)) {
                    Toast.makeText(MapActivity.this, "No Cnternet Connection...", Toast.LENGTH_SHORT).show();
                    return;
                }
                mActionsListener.scanBeacon();
                break;
            case R.id.bottom_sheet:
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    mActionsListener.expandBottomSheet(mBottomSheetBehavior);
                else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    mActionsListener.collapseBottomSheet(mBottomSheetBehavior);
                break;
            case R.id.btn_retry:
                if (!DeviceUtils.isInternetConnectionAvailable(MapActivity.this)) {
                    Toast.makeText(MapActivity.this, "No Internet Connection...", Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.mapUi.progressBar.setVisibility(View.VISIBLE);
                binding.mapUi.btnRetry.setVisibility(View.GONE);
                loadNearbyPlaces();
                break;
            case R.id.btn_sign_out:
                mActionsListener.signout();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_characteristic:
                mActionsListener.editCharacteristics();
//                Toast.makeText(MapActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_achievement:
                showAchievementActivity();
                return true;
            case R.id.menu_settings:
                showSettingsActivity();
                return true;
        }
        return false;
    }

    PlaceItemListener mItemListener = new PlaceItemListener() {
        @Override
        public void onPlaceClick(Place place) {
            mDestinationResult = saveToResult(place);
            updateDestinationText();
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }

        @Override
        public void onLikeClick(Result place) {
            mPlaceListManager.addFavouritePlace(place);
            mFavouritePlaceAdapter.setFavouritePlaces(mPlaceListManager.getFavouritePlaces());

            if (mPlaceListManager.getCount() > 0) {
                binding.navView.tvFavPlace.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onUnlikeClick(Result place) {
            mPlaceListManager.removeFavouritePlace(place);
            mFavouritePlaceAdapter.setFavouritePlaces(mPlaceListManager.getFavouritePlaces());
            mNearbyPlaceAdapter.notifyDataSetChanged();

            if (mPlaceListManager.getCount() == 0) {
                binding.navView.tvFavPlace.setVisibility(View.INVISIBLE);
            }
        }
    };

    public interface PlaceItemListener {
        void onPlaceClick(Place place);

        void onLikeClick(Result place);

        void onUnlikeClick(Result place);
    }

}
