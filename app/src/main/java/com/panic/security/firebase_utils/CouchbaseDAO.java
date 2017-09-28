package com.panic.security.firebase_utils;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.panic.security.R;
import com.panic.security.entities.Profile;
import com.panic.security.entities.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by david on 9/27/17.
 */

public class CouchbaseDAO {
    private Manager manager;
    private Database database;

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

        long birthday = 0;
        if (properties.get(FirebaseReferences.Profile.BIRTHDAY_REFERENCE).getClass().equals(Integer.class)){
            birthday = (int) properties.get(FirebaseReferences.Profile.BIRTHDAY_REFERENCE);
        }
        else {
            birthday = (long) properties.get(FirebaseReferences.Profile.BIRTHDAY_REFERENCE);
        }

        profile.setId((String) properties.get(FirebaseReferences.Profile.ID_REFERENCE));
        profile.setUser_id((String) properties.get(FirebaseReferences.Profile.USER_ID_REFERENCE));
        profile.setBirthday (birthday);
        profile.setCountry ((String) properties.get(FirebaseReferences.Profile.COUNTRY_REFERENCE));
        profile.setGender ((String) properties.get(FirebaseReferences.Profile.GENDER_REFERENCE));
        profile.setLast_name ((String) properties.get(FirebaseReferences.Profile.LAST_NAME_REFERENCE));
        profile.setName ((String) properties.get(FirebaseReferences.Profile.NAME_REFERENCE));

        return profile;
    }

    public void pushUser (User user) {
        Document document = database.getDocument (CouchbaseReferences.USER_REFERENCE);

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

    public void deleteData () {
        try {
            database.getDocument(CouchbaseReferences.USER_REFERENCE).delete();
            database.getDocument(CouchbaseReferences.PROFILE_REFERENCE).delete();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

}
