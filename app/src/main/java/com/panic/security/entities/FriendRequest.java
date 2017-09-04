package com.panic.security.entities;

/**
 * Created by maikb on 3/09/2017.
 */

public class FriendRequest {

    private String userId;
    private String date;

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
}
