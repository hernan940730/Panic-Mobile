package com.panic.security.entities;

import java.io.Serializable;

/**
 * Created by david on 10/5/17.
 */

public class Friend implements Serializable {
    private String user_id;
    private long date;
    private boolean is_location_shared;

    public Friend(){}

    public Friend(String user_id, long date, boolean is_location_shared) {
        this.user_id = user_id;
        this.date = date;
        this.is_location_shared = is_location_shared;
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

    public boolean getIs_location_shared() {
        return is_location_shared;
    }

    public void setLocationShared(boolean locationShared) {
        is_location_shared = locationShared;
    }

}
