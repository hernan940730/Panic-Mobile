package com.panic.security.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maikb on 3/09/2017.
 */

public class User {

    private String email;
    private HashMap<String, FriendRequestIn> friend_requests_in;
    private HashMap<String, FriendRequestOut> friend_requests_out;
    private HashMap<String, Friend> friends;
    private boolean is_active_account;
    private String phone_number;
    private String profile_id;
    private String profile_picture;
    private HashMap<String, String> reports;

    @Override
    public String toString() {
        return email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<String, FriendRequestIn> getFriend_requests_in() {
        return friend_requests_in;
    }

    public void setFriend_requests_in(HashMap<String, FriendRequestIn> friend_requests_in) {
        this.friend_requests_in = friend_requests_in;
    }

    public HashMap<String, FriendRequestOut> getFriend_requests_out() {
        return friend_requests_out;
    }

    public void setFriend_requests_out(HashMap<String, FriendRequestOut> friend_requests_out) {
        this.friend_requests_out = friend_requests_out;
    }

    public HashMap<String, Friend> getFriends() {
        return friends;
    }

    public void setFriends(HashMap<String, Friend> friends) {
        this.friends = friends;
    }

    public boolean getIs_active_account() {
        return is_active_account;
    }

    public void setIs_active_account(boolean activeAccount) {
        is_active_account = activeAccount;
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

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public HashMap<String, String> getReports() {
        return reports;
    }

    public void setReports(HashMap<String, String> reports) {
        this.reports = reports;
    }

    public static class FriendRequestIn {

        private long date;
        private String user_id;

        public String getUserId() {
            return user_id;
        }

        public void setUserId(String user_id) {
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

        public FriendRequestOut(){}

        public FriendRequestOut(String user_id, long date) {
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
