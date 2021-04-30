package com.abbvisor.abbvisor.model.beacon;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by natavit on 2/6/2017 AD.
 */

public class BeaconContent implements Parcelable {

    @SerializedName("template")
    @Expose
    private String template;

    @SerializedName("images_upload")
    @Expose
    private Object imagesUpload;

    @SerializedName("topics")
    @Expose
    private List<String> topics = null;

    @SerializedName("opentime")
    @Expose
    private String opentime;

    @SerializedName("closetime")
    @Expose
    private String closetime;

    @SerializedName("telephone")
    @Expose
    private String telephone;

    @SerializedName("contents")
    @Expose
    private List<String> contents = null;

    @SerializedName("prices")
    @Expose
    private List<String> prices = null;

    @SerializedName("images")
    @Expose
    private List<String> images = null;

    @SerializedName("activities")
    @Expose
    private List<BeaconActivity> activities;

    protected BeaconContent(Parcel in) {
        template = in.readString();
        topics = in.createStringArrayList();
        opentime = in.readString();
        closetime = in.readString();
        telephone = in.readString();
        contents = in.createStringArrayList();
        prices = in.createStringArrayList();
        images = in.createStringArrayList();
        activities = in.createTypedArrayList(BeaconActivity.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(template);
        dest.writeStringList(topics);
        dest.writeString(opentime);
        dest.writeString(closetime);
        dest.writeString(telephone);
        dest.writeStringList(contents);
        dest.writeStringList(prices);
        dest.writeStringList(images);
        dest.writeTypedList(activities);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BeaconContent> CREATOR = new Creator<BeaconContent>() {
        @Override
        public BeaconContent createFromParcel(Parcel in) {
            return new BeaconContent(in);
        }

        @Override
        public BeaconContent[] newArray(int size) {
            return new BeaconContent[size];
        }
    };

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Object getImagesUpload() {
        return imagesUpload;
    }

    public void setImagesUpload(Object imagesUpload) {
        this.imagesUpload = imagesUpload;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public List<String> getPrices() {
        return prices;
    }

    public void setPrices(List<String> prices) {
        this.prices = prices;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<BeaconActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<BeaconActivity> activities) {
        this.activities = activities;
    }
}
