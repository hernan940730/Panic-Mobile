package com.panic.security.entities;

/**
 * Created by maikb on 3/09/2017.
 */

public class Crime {

    private String id;
    private String report_id;
    private String location_id;
    private String type;
    private long date;

    public Crime() {
    }

    public Crime(String id, String report_id, String location_id, String type, long date) {
        this.id = id;
        this.report_id = report_id;
        this.location_id = location_id;
        this.type = type;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReport_id() {
        return report_id;
    }

    public void setReport_id(String report_id) {
        this.report_id = report_id;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

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
