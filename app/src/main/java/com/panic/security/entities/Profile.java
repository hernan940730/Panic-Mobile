package com.panic.security.entities;

import java.io.Serializable;

/**
 * Created by maikb on 3/09/2017.
 */

public class Profile implements Serializable {

    private String id;
    private String user_id;
    private long birthday;
    private String gender;
    private String last_name;
    private String name;
    private String country;

    public Profile() {
    }

    public Profile(String id, String user_id, long birthday, String gender, String last_name, String name, String country) {
        this.id = id;
        this.user_id = user_id;
        this.birthday = birthday;
        this.gender = gender;
        this.last_name = last_name;
        this.name = name;
        this.country = country;
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

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
