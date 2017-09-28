package com.panic.security.entities;

import java.util.Map;

/**
 * Created by maikb on 3/09/2017.
 */

public class Report {

    private String id;
    private String crime_id;
    private long date;
    private String description;
    private Map<String, String> stolen_objects;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Map<String, String> getStolen_objects() {
        return stolen_objects;
    }

    public void setStolen_objects(Map<String, String> stolen_objects) {
        this.stolen_objects = stolen_objects;
    }
}
