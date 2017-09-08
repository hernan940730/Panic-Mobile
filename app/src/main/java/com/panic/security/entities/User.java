package com.panic.security.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by maikb on 3/09/2017.
 */

public class User {

    private String email;
    private List<FriendRequestIn> friendRequestsIn;
    private HashMap<String, FriendRequestOut> friendRequestsOut;
    private HashMap<String, Friend> friends;
    private Boolean isActiveAccount;
    private String phoneNumber;
    private String profileId;
    private String profilePicture;
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

    public List<FriendRequestIn> getFriendRequestsIn() {
        return friendRequestsIn;
    }

    public void setFriendRequestsIn(List<FriendRequestIn> friendRequestsIn) {
        this.friendRequestsIn = friendRequestsIn;
    }

    public HashMap<String, FriendRequestOut> getFriendRequestsOut() {
        return friendRequestsOut;
    }

    public void setFriendRequestsOut(HashMap<String, FriendRequestOut> friendRequestsOut) {
        this.friendRequestsOut = friendRequestsOut;
    }

    public HashMap<String, Friend> getFriends() {
        return friends;
    }

    public void setFriends(HashMap<String, Friend> friends) {
        this.friends = friends;
    }

    public Boolean getActiveAccount() {
        return isActiveAccount;
    }

    public void setActiveAccount(Boolean activeAccount) {
        isActiveAccount = activeAccount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public HashMap<String, String> getReports() {
        return reports;
    }

    public void setReports(HashMap<String, String> reports) {
        this.reports = reports;
    }

    public class FriendRequestIn {

        private String userId;
        private Long date;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Long getDate() {
            return date;
        }

        public void setDate(Long date) {
            this.date = date;
        }
    }

    public class FriendRequestOut {

        private String userId;
        private Long date;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Long getDate() {
            return date;
        }

        public void setDate(Long date) {
            this.date = date;
        }
    }

    public class Friend {

        private String userId;
        private Long date;
        private Boolean isLocationShared;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Long getDate() {
            return date;
        }

        public void setDate(Long date) {
            this.date = date;
        }

        public Boolean getLocationShared() {
            return isLocationShared;
        }

        public void setLocationShared(Boolean locationShared) {
            isLocationShared = locationShared;
        }

    }


}
