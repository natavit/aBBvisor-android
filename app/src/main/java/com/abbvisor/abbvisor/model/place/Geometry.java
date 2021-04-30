package com.abbvisor.abbvisor.model.place;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Natavit on 1/29/2016 AD.
 */
public class Geometry implements Parcelable {

    @SerializedName("location")
    @Expose
    private Location location;

    public Geometry(Location loc) {
        location = loc;
    }

    protected Geometry(Parcel in) {
        location = in.readParcelable(android.location.Location.class.getClassLoader());
    }

    public static final Creator<Geometry> CREATOR = new Creator<Geometry>() {
        @Override
        public Geometry createFromParcel(Parcel in) {
            return new Geometry(in);
        }

        @Override
        public Geometry[] newArray(int size) {
            return new Geometry[size];
        }
    };

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(location, i);
    }
}
