package com.panic.security.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by maikb on 3/09/2017.
 */

public class Report implements Serializable {

    private String id;
    private String user_id;
    private String crime_id;
    private long date;
    private String description;

    public Report() {
    }

    public Report(String id, String user_id, String crime_id, long date, String description) {
        this.id = id;
        this.user_id = user_id;
        this.crime_id = crime_id;
        this.date = date;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCrime_id() {
        return crime_id;
    }

    public void setCrime_id(String crime_id) {
        this.crime_id = crime_id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
