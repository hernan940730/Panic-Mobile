package com.panic.security.entities;

/**
 * Created by maikb on 3/09/2017.
 */

public class Friend {

    private String userId;
    private String date;
    private Boolean isLocationShared;
    private Boolean isPending;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getLocationShared() {
        return isLocationShared;
    }

    public void setLocationShared(Boolean locationShared) {
        isLocationShared = locationShared;
    }

    public Boolean getPending() {
        return isPending;
    }

    public void setPending(Boolean pending) {
        isPending = pending;
    }
}
