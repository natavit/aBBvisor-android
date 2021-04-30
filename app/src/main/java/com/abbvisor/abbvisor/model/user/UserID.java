package com.abbvisor.abbvisor.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by natavit on 5/1/2017 AD.
 */

public class UserID {
    @SerializedName("value")
    @Expose
    private String id;
    @SerializedName("idType")
    @Expose
    private String idType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }
}
