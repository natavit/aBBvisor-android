package com.abbvisor.abbvisor.model.place;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by natavit on 10/29/2016 AD.
 */

public class AltId implements Parcelable {

    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("scope")
    @Expose
    private String scope;

    protected AltId(Parcel in) {
        placeId = in.readString();
        scope = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeId);
        dest.writeString(scope);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AltId> CREATOR = new Creator<AltId>() {
        @Override
        public AltId createFromParcel(Parcel in) {
            return new AltId(in);
        }

        @Override
        public AltId[] newArray(int size) {
            return new AltId[size];
        }
    };

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

}
