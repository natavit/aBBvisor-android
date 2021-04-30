package com.abbvisor.abbvisor.model.place.distancematrix;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by natavit on 2/26/2017 AD.
 */

public class DistanceMatrix implements Parcelable {
    @SerializedName("destination_addresses")
    @Expose
    private List<String> destinationAddresses = null;
    @SerializedName("origin_addresses")
    @Expose
    private List<String> originAddresses = null;
    @SerializedName("rows")
    @Expose
    private List<Row> rows = null;
    @SerializedName("status")
    @Expose
    private String status;

    protected DistanceMatrix(Parcel in) {
        destinationAddresses = in.createStringArrayList();
        originAddresses = in.createStringArrayList();
        rows = in.createTypedArrayList(Row.CREATOR);
        status = in.readString();
    }

    public static final Creator<DistanceMatrix> CREATOR = new Creator<DistanceMatrix>() {
        @Override
        public DistanceMatrix createFromParcel(Parcel in) {
            return new DistanceMatrix(in);
        }

        @Override
        public DistanceMatrix[] newArray(int size) {
            return new DistanceMatrix[size];
        }
    };

    public List<String> getDestinationAddresses() {
        return destinationAddresses;
    }

    public void setDestinationAddresses(List<String> destinationAddresses) {
        this.destinationAddresses = destinationAddresses;
    }

    public List<String> getOriginAddresses() {
        return originAddresses;
    }

    public void setOriginAddresses(List<String> originAddresses) {
        this.originAddresses = originAddresses;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(destinationAddresses);
        parcel.writeStringList(originAddresses);
        parcel.writeTypedList(rows);
        parcel.writeString(status);
    }
}
