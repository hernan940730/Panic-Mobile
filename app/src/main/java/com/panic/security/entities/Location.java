package com.panic.security.entities;

import java.io.Serializable;

/**
 * Created by maikb on 3/09/2017.
 */

public class Location implements Serializable {

    private String id;
    private String crime_id;
    private double latitude;
    private double longitude;

    public Location() {
    }

    public Location(String id, String crime_id, double latitude, double longitude) {
        this.id = id;
        this.crime_id = crime_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
