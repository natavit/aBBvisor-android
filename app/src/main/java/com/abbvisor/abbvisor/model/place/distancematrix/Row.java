package com.abbvisor.abbvisor.model.place.distancematrix;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by natavit on 2/26/2017 AD.
 */

public class Row implements Parcelable {

    @SerializedName("elements")
    @Expose
    private List<Element> elements = null;

    protected Row(Parcel in) {
        elements = in.createTypedArrayList(Element.CREATOR);
    }

    public static final Creator<Row> CREATOR = new Creator<Row>() {
        @Override
        public Row createFromParcel(Parcel in) {
            return new Row(in);
        }

        @Override
        public Row[] newArray(int size) {
            return new Row[size];
        }
    };

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(elements);
    }
}
