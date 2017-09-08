package com.panic.security.entities;

/**
 * Created by maikb on 3/09/2017.
 */

public class Crime {

    private String type;
    private Long date;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
