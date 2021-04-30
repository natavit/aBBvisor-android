package com.abbvisor.abbvisor.model.achievement;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by natavit on 4/3/2017 AD.
 */

public class Result implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("max")
    @Expose
    private int max;
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("achievement_detail")
    @Expose
    private String achievementDetail;

    protected Result(Parcel in) {
        name = in.readString();
        max = in.readInt();
        count = in.readInt();
        achievementDetail = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(max);
        dest.writeInt(count);
        dest.writeString(achievementDetail);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getAchievementDetail() {
        return achievementDetail;
    }

    public void setAchievementDetail(String achievementDetail) {
        this.achievementDetail = achievementDetail;
    }
}
