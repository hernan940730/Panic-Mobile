package com.panic.security.entities;

import java.util.HashMap;
import java.util.List;

/**
 * Created by maikb on 3/09/2017.
 */

public class Report {

    private String crimeId;
    private Long date;
    private String description;
    private String locationId;
    private HashMap<String, String> stolenObjects;

    public String getCrimeId() {
        return crimeId;
    }

    public void setCrimeId(String crimeId) {
        this.crimeId = crimeId;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public HashMap<String, String> getStolenObjects() {
        return stolenObjects;
    }

    public void setStolenObjects(HashMap<String, String> stolenObjects) {
        this.stolenObjects = stolenObjects;
    }
}
