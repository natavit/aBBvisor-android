package com.abbvisor.abbvisor.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.abbvisor.abbvisor.model.place.PlaceList;
import com.abbvisor.abbvisor.model.place.Result;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;

public class PlaceListManager {

    private static final String TAG = PlaceListManager.class.getSimpleName();

    private Context mContext;
    private ArrayList<Result> mFavouritePlaces;

    public PlaceListManager(Context context) {
        mContext = context;
        loadCache();
    }

    public ArrayList<Result> getFavouritePlaces() {
        return mFavouritePlaces;
    }

    public void setPlaces(ArrayList<Result> places) {
        mFavouritePlaces = places;
        if (mFavouritePlaces != null) {
            // Save to Persistent Storage
            saveCache();
        }
    }

    public boolean isFavouritePlace(Result place) {
        if (mFavouritePlaces == null)
            return false;

        for (Result p : mFavouritePlaces) {
            if (p.getPlaceId().equals(place.getPlaceId()))
                return true;
        }

        return false;
    }

    public void addFavouritePlace(Result place) {
        if (mFavouritePlaces == null)
            mFavouritePlaces = new ArrayList<>();

        mFavouritePlaces.add(place);

        // Save to Persistent Storage
        saveCache();
    }

    public void removeFavouritePlace(Result place) {

        for (Iterator<Result> it = mFavouritePlaces.iterator(); it.hasNext();) {
            Result p = it.next();
            if (p.getPlaceId().equals(place.getPlaceId())) {
                it.remove();
            }
        }

//        for (Result p : mFavouritePlaces) {
//            if (p.getPlaceId().equals(place.getPlaceId()))
//                mFavouritePlaces.remove(p);
//        }
        saveCache();
    }

    public int getCount() {
        if (mFavouritePlaces == null) return 0;
        return mFavouritePlaces.size();
    }

    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putParcelableArrayList("dao", mFavouritePlaces);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mFavouritePlaces = savedInstanceState.getParcelable("dao");
    }

    private void saveCache() {
        PlaceList cacheDao = new PlaceList();

        if (mFavouritePlaces != null)
            cacheDao.setResults(mFavouritePlaces);

        // Convert object to json
        String json = new Gson().toJson(cacheDao);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SharedPreferences prefs = mContext.getSharedPreferences(uid+"places",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("json", json);
        editor.apply();
    }

    private void loadCache() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SharedPreferences prefs = mContext.getSharedPreferences(uid+"places",
                Context.MODE_PRIVATE);
        String json = prefs.getString("json", null);
        if (json == null)
            return;

        PlaceList pl = new Gson().fromJson(json, PlaceList.class);
        mFavouritePlaces = pl.getResults();
    }

}
