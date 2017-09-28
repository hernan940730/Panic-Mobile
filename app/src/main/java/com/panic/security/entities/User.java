package com.panic.security.entities;

import com.google.firebase.auth.ActionCodeResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maikb on 3/09/2017.
 */

public class User {

    private String id;
    private String email;
    private Map<String, FriendRequestIn> friend_requests_in;
    private Map<String, FriendRequestOut> friend_requests_out;
    private Map<String, Friend> friends;
    private boolean is_active_account;
    private String phone_number;
    private String profile_id;
    private Map<String, String> reports;

    public User () { }

    public User (String id, String email, Map<String, FriendRequestIn> friend_requests_in, Map<String, FriendRequestOut> friend_requests_out, Map<String, Friend> friends, boolean is_active_account, String phone_number, String profile_id, Map<String, String> reports) {
        this.id = id;
        this.email = email;
        this.friend_requests_in = friend_requests_in;
        this.friend_requests_out = friend_requests_out;
        this.friends = friends;
        this.is_active_account = is_active_account;
        this.phone_number = phone_number;
        this.profile_id = profile_id;
        this.reports = reports;
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

    public Map<String, FriendRequestIn> getFriend_requests_in() {
        return friend_requests_in;
    }

    public void setFriend_requests_in(Map<String, FriendRequestIn> friend_requests_in) {
        this.friend_requests_in = friend_requests_in;
    }

    public Map<String, FriendRequestOut> getFriend_requests_out() {
        return friend_requests_out;
    }

    public void setFriend_requests_out(Map<String, FriendRequestOut> friend_requests_out) {
        this.friend_requests_out = friend_requests_out;
    }

    public Map<String, Friend> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, Friend> friends) {
        this.friends = friends;
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

    public Map<String, String> getReports() {
        return reports;
    }

    public void setReports(Map<String, String> reports) {
        this.reports = reports;
    }

    public static class FriendRequestIn {

        private String user_id;
        private long date;

        public FriendRequestIn() {

        }

        public FriendRequestIn(String user_id, long date) {
            this.user_id = user_id;
            this.date = date;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }
    }

    public static class FriendRequestOut {

        private String user_id;
        private long date;

        public FriendRequestOut () { }

        public FriendRequestOut (String user_id, long date) {
            this.user_id = user_id;
            this.date = date;
        }

        public String getUser_id() { return user_id; }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public long getDate() { return date; }

        public void setDate(long date) { this.date = date; }

    }

    public static class Friend {

        private String user_id;
        private long date;
        private boolean is_location_shared;

        public Friend () { }

        public Friend(String user_id, long date, boolean is_location_shared) {
            this.user_id = user_id;
            this.date = date;
            this.is_location_shared = is_location_shared;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public boolean getIs_location_shared() {
            return is_location_shared;
        }

        public void setLocationShared(boolean locationShared) {
            is_location_shared = locationShared;
        }

    }

}
