package com.panic.security.utils;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.panic.security.R;
import com.panic.security.entities.Crime;
import com.panic.security.entities.Location;
import com.panic.security.entities.Profile;
import com.panic.security.entities.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 9/27/17.
 */

public class CouchbaseDAO {
    private Manager manager;
    private Database database;
    private boolean isUserListenerSet;

    private static CouchbaseDAO couchbaseDAO;

    private CouchbaseDAO (Activity activity) {
        try {
            manager = new Manager (new AndroidContext(activity.getApplicationContext()),
                    Manager.DEFAULT_OPTIONS);
            database = manager.getDatabase (CouchbaseReferences.APP_NAME_REFERENCE);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText (activity.getApplicationContext(), R.string.couchbase_error, Toast.LENGTH_SHORT)
                    .show();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            Toast.makeText (activity.getApplicationContext(), R.string.couchbase_error, Toast.LENGTH_SHORT)
                    .show();
        }
        isUserListenerSet = false;
    }

    @Nullable
    public static CouchbaseDAO getInstance () {
        return couchbaseDAO;
    }

    public static synchronized CouchbaseDAO getInstance (Activity activity) {
        if (couchbaseDAO == null) {
            couchbaseDAO = new CouchbaseDAO (activity);
        }
        return couchbaseDAO;
    }

    public User getUser () {
        Document document = database.getDocument (CouchbaseReferences.USER_REFERENCE);
        Map<String, Object> properties = document.getProperties();

        if (properties == null || properties.isEmpty()) {
            return null;
        }

        User user = new User (
                (String) properties.get(FirebaseReferences.User.ID_REFERENCE),
                (String) properties.get(FirebaseReferences.User.EMAIL_REFERENCE),
                (HashMap<String, User.FriendRequestIn>) properties.get(FirebaseReferences.User.FRIEND_REQUESTS_IN_REFERENCE),
                (HashMap<String, User.FriendRequestOut>) properties.get(FirebaseReferences.User.FRIEND_REQUESTS_OUT_REFERENCE),
                (HashMap<String, User.Friend>) properties.get(FirebaseReferences.User.FRIENDS_REFERENCE),
                (boolean) properties.get(FirebaseReferences.User.IS_ACTIVE_REFERENCE),
                (String) properties.get(FirebaseReferences.User.PHONE_NUMBER_REFERENCE),
                (String) properties.get(FirebaseReferences.User.PROFILE_ID_REFERENCE),
                (HashMap<String, String>) properties.get(FirebaseReferences.User.REPORTS_REFERENCE));
        return user;
    }

    @Nullable
    public Profile getProfile () {
        Document document = database.getDocument(CouchbaseReferences.PROFILE_REFERENCE);
        Map<String, Object> properties = document.getProperties();

        if (properties == null || properties.isEmpty()) {
            return null;
        }

        Profile profile = new Profile();

        profile.setId((String) properties.get(FirebaseReferences.Profile.ID_REFERENCE));
        profile.setUser_id((String) properties.get(FirebaseReferences.Profile.USER_ID_REFERENCE));
        profile.setBirthday (getLongValue(properties.get(FirebaseReferences.Profile.BIRTHDAY_REFERENCE)));
        profile.setCountry ((String) properties.get(FirebaseReferences.Profile.COUNTRY_REFERENCE));
        profile.setGender ((String) properties.get(FirebaseReferences.Profile.GENDER_REFERENCE));
        profile.setLast_name ((String) properties.get(FirebaseReferences.Profile.LAST_NAME_REFERENCE));
        profile.setName ((String) properties.get(FirebaseReferences.Profile.NAME_REFERENCE));

        return profile;
    }

    private long getLongValue (Object value) {
        long ret = 0;
        if (value.getClass().equals(Integer.class)){
            ret = (int) value;
        }
        else {
            ret = (long) value;
        }
        return ret;
    }

    @Nullable
    public List<Pair<Crime, Location>> getCrimeLocationList() {
        Document document = database.getDocument (CouchbaseReferences.CRIME_LOCATION_LIST_REFERENCE);
        Map<String, Object> properties = document.getProperties();

        if (properties == null || properties.isEmpty()) {
            return null;
        }

        List<Pair<Crime, Location>> list = new ArrayList<>();

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if(!entry.getValue().getClass().equals(Map.class)) {
                continue;
            }
            Map<String, Object> subProperties = (Map<String, Object>) entry.getValue();

            Crime crime = new Crime(
                    (String) subProperties.get(CouchbaseReferences.CrimeLocationList.CRIME_ID_REFERENCE),
                    (String) subProperties.get(CouchbaseReferences.CrimeLocationList.REPORT_ID_REFERENCE),
                    (String) subProperties.get(CouchbaseReferences.CrimeLocationList.LOCATION_ID_REFERENCE),
                    (String) subProperties.get(CouchbaseReferences.CrimeLocationList.TYPE_REFERENCE),
                    getLongValue (subProperties.get(CouchbaseReferences.CrimeLocationList.DATE_REFERENCE))
            );
            Location location = new Location (
                    (String) subProperties.get(CouchbaseReferences.CrimeLocationList.LOCATION_ID_REFERENCE),
                    (String) subProperties.get(CouchbaseReferences.CrimeLocationList.CRIME_ID_REFERENCE),
                    (double) subProperties.get(CouchbaseReferences.CrimeLocationList.LATITUDE_REFERENCE),
                    (double) subProperties.get(CouchbaseReferences.CrimeLocationList.LONGITUDE_REFERENCE)
            );

            list.add (new Pair<> (crime, location));
        }
        return list;
    }

    public void pushUser (User user) {
        Document document = database.getDocument (CouchbaseReferences.USER_REFERENCE);

        if(user == null) {
            return;
        }
        isUserListenerSet = true;
        Map<String, Object> properties = new HashMap<>();
        properties.put(FirebaseReferences.User.ID_REFERENCE, user.getId());
        properties.put(FirebaseReferences.User.EMAIL_REFERENCE, user.getEmail());
        properties.put(FirebaseReferences.User.FRIEND_REQUESTS_IN_REFERENCE, user.getFriend_requests_in());
        properties.put(FirebaseReferences.User.FRIEND_REQUESTS_OUT_REFERENCE, user.getFriend_requests_out());
        properties.put(FirebaseReferences.User.FRIENDS_REFERENCE, user.getFriends());
        properties.put(FirebaseReferences.User.IS_ACTIVE_REFERENCE, user.getIs_active_account());
        properties.put(FirebaseReferences.User.PHONE_NUMBER_REFERENCE, user.getPhone_number());
        properties.put(FirebaseReferences.User.PROFILE_ID_REFERENCE, user.getProfile_id());
        properties.put(FirebaseReferences.User.REPORTS_REFERENCE, user.getReports());

        try {
            document.putProperties (properties);

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void pushProfile (Profile profile) {
        Document document = database.getDocument(CouchbaseReferences.PROFILE_REFERENCE);

        Map<String, Object> properties = new HashMap<>();
        properties.put(FirebaseReferences.Profile.ID_REFERENCE, profile.getId());
        properties.put(FirebaseReferences.Profile.USER_ID_REFERENCE, profile.getUser_id());
        properties.put(FirebaseReferences.Profile.BIRTHDAY_REFERENCE, profile.getBirthday());
        properties.put(FirebaseReferences.Profile.COUNTRY_REFERENCE, profile.getCountry());
        properties.put(FirebaseReferences.Profile.GENDER_REFERENCE, profile.getGender());
        properties.put(FirebaseReferences.Profile.LAST_NAME_REFERENCE, profile.getLast_name());
        properties.put(FirebaseReferences.Profile.NAME_REFERENCE, profile.getName());

        try {
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void pushCrimeLocationList (List<Pair<Crime, Location>> list) {
        Document document = database.getDocument(CouchbaseReferences.CRIME_LOCATION_LIST_REFERENCE);
        Map<String, Object> properties = new HashMap<>();
        for (Pair<Crime, Location> pair : list) {
            Crime crime = pair.first;
            Location location = pair.second;
            Map<String, Object> subProperties = getCrimeLocationSubProperties(crime, location);
            properties.put("report_" + crime.getReport_id(), subProperties);
        }
        try {
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }


    private Map<String, Object> getCrimeLocationSubProperties(Crime crime, Location location) {
        Map<String, Object> subProperties = new HashMap<>();
        subProperties.put(CouchbaseReferences.CrimeLocationList.CRIME_ID_REFERENCE, crime.getId());
        subProperties.put(CouchbaseReferences.CrimeLocationList.DATE_REFERENCE, crime.getDate());
        subProperties.put(CouchbaseReferences.CrimeLocationList.LATITUDE_REFERENCE, location.getLatitude());
        subProperties.put(CouchbaseReferences.CrimeLocationList.LOCATION_ID_REFERENCE, location.getId());
        subProperties.put(CouchbaseReferences.CrimeLocationList.LONGITUDE_REFERENCE, location.getLongitude());
        subProperties.put(CouchbaseReferences.CrimeLocationList.REPORT_ID_REFERENCE, crime.getReport_id());
        subProperties.put(CouchbaseReferences.CrimeLocationList.TYPE_REFERENCE, crime.getType());
        return subProperties;
    }

    public void deleteData () {
        try {
            database.getDocument(CouchbaseReferences.USER_REFERENCE).delete();
            database.getDocument(CouchbaseReferences.PROFILE_REFERENCE).delete();
            database.getDocument(CouchbaseReferences.CRIME_LOCATION_LIST_REFERENCE).delete();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public boolean getIsUserListenerSet() {
        return isUserListenerSet;
    }
}