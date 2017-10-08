package com.panic.security.entities;

import java.io.Serializable;

/**
 * Created by david on 10/5/17.
 */

public class FriendRequest implements Serializable {
    private long date;
    private String user_id;

    public FriendRequest() {}

    public FriendRequest (String user_id, long date) {
        this.user_id = user_id;
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
