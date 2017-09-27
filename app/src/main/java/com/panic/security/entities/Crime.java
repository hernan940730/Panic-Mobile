package com.panic.security.entities;

import java.io.Serializable;

/**
 * Created by maikb on 3/09/2017.
 */

public class Crime implements Serializable {

    private String type;
    private long date;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
