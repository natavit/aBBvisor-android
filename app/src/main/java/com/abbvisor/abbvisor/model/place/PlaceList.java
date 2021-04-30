package com.abbvisor.abbvisor.model.place;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Natavit on 1/29/2016 AD.
 */
public class PlaceList implements Parcelable {

    @SerializedName("status")
    private String status;

    @SerializedName("results")
    private ArrayList<Result> results = new ArrayList<Result>();

    @SerializedName("next_page_token")
    private String nextPageToken;

    public PlaceList() {}

    protected PlaceList(Parcel in) {
        status = in.readString();
        results = in.createTypedArrayList(Result.CREATOR);
        nextPageToken = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeTypedList(results);
        dest.writeString(nextPageToken);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlaceList> CREATOR = new Creator<PlaceList>() {
        @Override
        public PlaceList createFromParcel(Parcel in) {
            return new PlaceList(in);
        }

        @Override
        public PlaceList[] newArray(int size) {
            return new PlaceList[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
}
