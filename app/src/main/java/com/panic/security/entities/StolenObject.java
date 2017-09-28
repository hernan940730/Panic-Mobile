package com.panic.security.entities;

/**
 * Created by maikb on 3/09/2017.
 */

public class StolenObject {

    private String id;
    private String report_id;
    private String name;
    private String description;

    public StolenObject() {
    }

    public StolenObject(String id, String report_id, String name, String description) {
        this.id = id;
        this.report_id = report_id;
        this.name = name;
        this.description = description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
