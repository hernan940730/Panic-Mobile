package com.panic.security.entities;

import com.google.firebase.auth.ActionCodeResult;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by maikb on 3/09/2017.
 */

public class User implements Serializable {

    private String id;
    private String email;
    private boolean is_active_account;
    private String phone_number;
    private String profile_id;

    public User () { }

    public User (String id, String email, boolean is_active_account, String phone_number, String profile_id) {
        this.id = id;
        this.email = email;
        this.is_active_account = is_active_account;
        this.phone_number = phone_number;
        this.profile_id = profile_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getIs_active_account() {
        return is_active_account;
    }

    public void setIs_active_account(boolean is_active_account) {
        this.is_active_account = is_active_account;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

}
