package com.panic.security.entities;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by maikb on 3/09/2017.
 */

public class Report implements Serializable {

    private String crime_id;
    private long date;
    private String description;
    private String location_id;
    private HashMap<String, String> stolen_objects;

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

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public HashMap<String, String> getStolen_objects() {
        return stolen_objects;
    }

    public void setStolen_objects(HashMap<String, String> stolen_objects) {
        this.stolen_objects = stolen_objects;
    }
}
