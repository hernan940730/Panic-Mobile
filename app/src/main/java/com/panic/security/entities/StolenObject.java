package com.panic.security.entities;

/**
 * Created by maikb on 3/09/2017.
 */

public class StolenObject {

    private String reportId;
    private String name;
    private String description;

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
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
