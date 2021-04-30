package com.abbvisor.abbvisor.model.place;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.abbvisor.abbvisor.model.place.distancematrix.DistanceMatrix;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by natavit on 10/29/2016 AD.
 */

public class Result implements Parcelable, Place {

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    @SerializedName("icon")
    @Expose
    private String icon;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;

    @SerializedName("photos")
    @Expose
    private List<Photo> photos = new ArrayList<>();

    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("scope")
    @Expose
    private String scope;

    @SerializedName("alt_ids")
    @Expose
    private List<AltId> altIds = new ArrayList<>();

    @SerializedName("reference")
    @Expose
    private String reference;

    @SerializedName("types")
    @Expose
    private List<String> types = new ArrayList<>();

    @SerializedName("vicinity")
    @Expose
    private String vicinity;

    private DistanceMatrix distanceMatrix;

    private String address;

    private String phoneNumber;

    private float rating;

    private List<Integer> placeTypes;

    public Result() {}

    protected Result(Parcel in) {
        geometry = in.readParcelable(Geometry.class.getClassLoader());
        icon = in.readString();
        id = in.readString();
        name = in.readString();
        placeId = in.readString();
        scope = in.readString();
        reference = in.readString();
        types = in.createStringArrayList();
        vicinity = in.readString();
        distanceMatrix = in.readParcelable(DistanceMatrix.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(geometry, flags);
        dest.writeString(icon);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(placeId);
        dest.writeString(scope);
        dest.writeString(reference);
        dest.writeStringList(types);
        dest.writeString(vicinity);
        dest.writeParcelable(distanceMatrix, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    /**
     * @return The geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * @param geometry The geometry
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * @return The icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon The icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The openingHours
     */
    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    /**
     * @param openingHours The opening_hours
     */
    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    /**
     * @return The photos
     */
    public List<Photo> getPhotos() {
        return photos;
    }

    /**
     * @param photos The photos
     */
    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    /**
     * @return The placeId
     */
    public String getPlaceId() {
        return placeId;
    }

    /**
     * @param placeId The place_id
     */
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    /**
     * @return The scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * @param scope The scope
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * @return The altIds
     */
    public List<AltId> getAltIds() {
        return altIds;
    }

    /**
     * @param altIds The alt_ids
     */
    public void setAltIds(List<AltId> altIds) {
        this.altIds = altIds;
    }

    /**
     * @return The reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * @param reference The reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * @return The types
     */
    public List<String> getTypes() {
        return types;
    }

    /**
     * @param types The types
     */
    public void setTypes(List<String> types) {
        this.types = types;
    }

    /**
     * @return The vicinity
     */
    public String getVicinity() {
        return vicinity;
    }

    /**
     * @param vicinity The vicinity
     */
    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public DistanceMatrix getDistanceMatrix() {
        return distanceMatrix;
    }

    public void setDistanceMatrix(DistanceMatrix distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setPlaceTypes(List<Integer> placeTypes) {
        this.placeTypes = placeTypes;
    }

    /*
    *
    *  Place
    *
     */

    @Override
    public List<Integer> getPlaceTypes() {
        return placeTypes;
    }

    @Override
    public CharSequence getAddress() {
        return address;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public LatLng getLatLng() {
        return geometry.getLocation().getLatLng();
    }

    @Override
    public LatLngBounds getViewport() {
        return null;
    }

    @Override
    public Uri getWebsiteUri() {
        return null;
    }

    @Override
    public CharSequence getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public float getRating() {
        return rating;
    }

    @Override
    public int getPriceLevel() {
        return 0;
    }

    @Override
    public CharSequence getAttributions() {
        return null;
    }

    @Override
    public Place freeze() {
        return null;
    }

    @Override
    public boolean isDataValid() {
        return false;
    }
}
