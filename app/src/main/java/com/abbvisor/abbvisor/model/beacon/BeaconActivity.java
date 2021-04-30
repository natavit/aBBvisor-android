package com.abbvisor.abbvisor.model.beacon;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by natavit on 4/10/2017 AD.
 */

public class BeaconActivity implements Parcelable {
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("opentime")
    @Expose
    private String opentime;
    @SerializedName("closetime")
    @Expose
    private String closetime;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("date_start")
    @Expose
    private String dateStart;
    @SerializedName("date_end")
    @Expose
    private String dateEnd;
    @SerializedName("days")
    @Expose
    private List<String> days = null;
    @SerializedName("score")
    @Expose
    private Double score;
    @SerializedName("dayloop")
    @Expose
    private boolean dayloop;

    protected BeaconActivity(Parcel in) {
        title = in.readString();
        description = in.readString();
        opentime = in.readString();
        closetime = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        image = in.readString();
        dateStart = in.readString();
        dateEnd = in.readString();
        days = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(opentime);
        dest.writeString(closetime);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(image);
        dest.writeString(dateStart);
        dest.writeString(dateEnd);
        dest.writeStringList(days);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BeaconActivity> CREATOR = new Creator<BeaconActivity>() {
        @Override
        public BeaconActivity createFromParcel(Parcel in) {
            return new BeaconActivity(in);
        }

        @Override
        public BeaconActivity[] newArray(int size) {
            return new BeaconActivity[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }

    public String getClosetime() {
        return closetime;
    }

    public void setClosetime(String closetime) {
        this.closetime = closetime;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public boolean isDayloop() {
        return dayloop;
    }

    public void setDayloop(boolean dayloop) {
        this.dayloop = dayloop;
    }
}
