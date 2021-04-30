package com.abbvisor.abbvisor.manager.api;

import com.abbvisor.abbvisor.model.achievement.Achievement;
import com.abbvisor.abbvisor.model.beacon.BeaconContent;
import com.abbvisor.abbvisor.model.place.PlaceList;
import com.abbvisor.abbvisor.model.place.distancematrix.DistanceMatrix;
import com.abbvisor.abbvisor.model.user.UserProfile;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Natavit on 2/12/2016 AD.
 */
public interface ServiceApi {

    /**
     * App API
     */

    @GET("customer?")
    Observable<UserProfile> getUserProfile(
            @Query("customerId") String id
    );

    @GET("beaconContent?")
    Observable<BeaconContent> getBeaconContent(
            @Query("major") String majorId,
            @Query("minor") String minorId,
            @Query("customerId") String id
    );

    @GET("customer/getAchievements?")
    Observable<Achievement> getAchievements(
            @Query("customerId") String id
    );

    @POST("customer/preferences")
    Observable<ResponseBody> savePreferences(
            @Body RequestBody body
    );

    @POST("customer/login_social")
    Observable<ResponseBody> trackUser(
            @Body RequestBody body
    );

    /**
     * Google Nearby place API
     */

    @POST
    Observable<PlaceList> loadNearbyPlaceList(
            @Url String url,
            @Query("key") String key,
            @Query("location") String location,
            @Query("radius") int radius
    );

    @POST
    Observable<PlaceList> loadNextNearbyPlaceList(
            @Url String url,
            @Query("key") String key,
            @Query("pagetoken") String pagetoken
    );

    @POST
    Observable<DistanceMatrix> findDistanceMatrix(
            @Url String url,
            @Query("key") String key,
            @Query("origins") String origin,
            @Query("destinations") String destinations
    );

}
