package com.example.q.project2;

/**
 * Created by q on 2017-01-02.
 */

public class TabCProfile {
    String userID;
    String userName;
    String profileURL;
    Boolean awake;
    String phone;
    Integer keycode;
    // TODO : Add counterpart?? and his keycode?


    public TabCProfile(String userID, String userName, String profileURL, Boolean awake, String phone, Integer keycode) {
        this.userID = userID;
        this.userName = userName;
        this.profileURL = profileURL;
        this.awake = awake;
        this.phone = phone;
        this.keycode = keycode;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public Boolean getAwake() {
        return awake;
    }

    public void setAwake(Boolean awake) {
        this.awake = awake;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getKeycode() {
        return keycode;
    }

    public void setKeycode(Integer keycode) {
        this.keycode = keycode;
    }
}
