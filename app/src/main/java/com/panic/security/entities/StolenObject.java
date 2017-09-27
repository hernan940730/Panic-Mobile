package com.panic.security.entities;

import java.io.Serializable;

/**
 * Created by maikb on 3/09/2017.
 */

public class StolenObject implements Serializable{

    private String name;
    private String description;

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
