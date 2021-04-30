package com.abbvisor.abbvisor.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by natavit on 4/8/2017 AD.
 */

public class UserProfile {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("preferences")
    @Expose
    private List<String> preferences = null;
    @SerializedName("logInInfos")
    @Expose
    private List<String> logInInfos = null;
    @SerializedName("achievements")
    @Expose
    private List<Object> achievements = null;
    @SerializedName("customerId")
    @Expose
    private UserID userID;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }

    public List<String> getLogInInfos() {
        return logInInfos;
    }

    public void setLogInInfos(List<String> logInInfos) {
        this.logInInfos = logInInfos;
    }

    public List<Object> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Object> achievements) {
        this.achievements = achievements;
    }

    public UserID getUserID() {
        return userID;
    }

    public void setUserID(UserID userID) {
        this.userID = userID;
    }
}
