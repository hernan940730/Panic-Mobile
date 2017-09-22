package com.panic.security.firebase_utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.panic.security.entities.Crime;
import com.panic.security.entities.Location;
import com.panic.security.entities.Profile;
import com.panic.security.entities.Report;
import com.panic.security.entities.StolenObject;
import com.panic.security.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 9/7/17.
 */

public class FirebaseDAO {
    private FirebaseDatabase database;
    private static FirebaseDAO firebaseDAO;

    private FirebaseDAO () {
        database = FirebaseDatabase.getInstance();
    }

    public static synchronized FirebaseDAO getInstance () {
        if (firebaseDAO == null) {
            firebaseDAO = new FirebaseDAO ();
        }
        return firebaseDAO;
    }

    public void getUserByID(String ID, final DataCallback<User> callback) {
        DatabaseReference ref = database.getReference(FirebaseReferences.USERS_REFERENCE).child(ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User entity = dataSnapshot.getValue (User.class);
                callback.onDataReceive (entity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getCrimeByID (String ID, final DataCallback<Crime> callback) {
        DatabaseReference ref = database.getReference(FirebaseReferences.CRIMES_REFERENCE).child(ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Crime entity = dataSnapshot.getValue (Crime.class);
                callback.onDataReceive (entity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getLocationByID (String ID, final DataCallback<Location> callback) {
        DatabaseReference ref = database.getReference(FirebaseReferences.LOCATIONS_REFERENCE).child(ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Location entity = dataSnapshot.getValue (Location.class);
                callback.onDataReceive (entity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getProfileByID (String ID, final DataCallback<Profile> callback) {
        DatabaseReference ref = database.getReference(FirebaseReferences.PROFILES_REFERENCE).child(ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile entity = dataSnapshot.getValue (Profile.class);
                callback.onDataReceive (entity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getAllFullNamesForAllProfiles(final DataCallback< List<String> > callback) {
        DatabaseReference ref = database.getReference().child(FirebaseReferences.PROFILES_REFERENCE);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> list = new ArrayList<String>();
                Map<String,Object> profiles =  (Map<String,Object>) dataSnapshot.getValue();
                for (Map.Entry<String, Object> entry : profiles.entrySet()){
                    Map singleProfile = (Map) entry.getValue();
                    String name = (String)singleProfile.get(FirebaseReferences.Profile.NAME_REFERENCE);
                    String lastName = (String)singleProfile.get(FirebaseReferences.Profile.LAST_NAME_REFERENCE);
                    list.add(name + " " + lastName);
                }
                callback.onDataReceive (list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getReportByID (String ID, final DataCallback<Report> callback) {
        DatabaseReference ref = database.getReference(FirebaseReferences.REPORTS_REFERENCE).child(ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Report entity = dataSnapshot.getValue (Report.class);
                callback.onDataReceive (entity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getStolenObjectByID (String ID, final DataCallback<StolenObject> callback) {
        DatabaseReference ref = database.getReference(FirebaseReferences.STOLEN_OBJECTS_REFERENCE).child(ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StolenObject entity = dataSnapshot.getValue (StolenObject.class);
                callback.onDataReceive (entity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getProfileIDByFullName(final String fullName, final DataCallback< String > callback) {

        DatabaseReference ref = database.getReference().child(FirebaseReferences.PROFILES_REFERENCE);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> profiles =  (Map<String,Object>) dataSnapshot.getValue();
                for (Map.Entry<String, Object> entry : profiles.entrySet()){
                    Map singleProfile = (Map) entry.getValue();
                    String name = (String)singleProfile.get(FirebaseReferences.Profile.NAME_REFERENCE);
                    String lastName = (String)singleProfile.get(FirebaseReferences.Profile.LAST_NAME_REFERENCE);
                    if(fullName.equals( (name + " " + lastName) )){
                        callback.onDataReceive (entry.getKey());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getUserIDByProfileID(final String profileID, final DataCallback<String> callback) {

        DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> users =  (Map<String,Object>) dataSnapshot.getValue();
                for (Map.Entry<String, Object> entry : users.entrySet()){
                    Map singleUser = (Map) entry.getValue();
                    String userProfileID = (String) singleUser.get(FirebaseReferences.User.PROFILE_ID_REFERENCE);
                    if(userProfileID.equals(profileID)){
                        callback.onDataReceive (entry.getKey());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });

    }

    public void areFriends(String currentUserID, String userID, final DataCallback< User.Friend > callback) {
        DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(currentUserID).child(FirebaseReferences.User.FRIENDS_REFERENCE).child(userID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User.Friend entity = dataSnapshot.getValue (User.Friend.class);
                callback.onDataReceive (entity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void areFriendRequestOut(String currentUserID, String userID, final DataCallback< User.Friend > callback) {
        DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(currentUserID).child(FirebaseReferences.User.FRIEND_REQUESTS_OUT_REFERENCE).child(userID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User.Friend entity = dataSnapshot.getValue (User.Friend.class);
                callback.onDataReceive (entity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public String pushUser (String ID, User entity) {
        DatabaseReference ref = database.getReference (FirebaseReferences.USERS_REFERENCE).child (ID);
        ref.setValue (entity);
        return ref.getKey ();
    }

    public String pushProfile (Profile entity) {
        DatabaseReference ref = database.getReference (FirebaseReferences.PROFILES_REFERENCE).push ();
        ref.setValue (entity);
        return ref.getKey ();
    }

    public String pushCrime (Crime entity) {
        DatabaseReference ref = database.getReference (FirebaseReferences.CRIMES_REFERENCE).push ();
        ref.setValue (entity);
        return ref.getKey ();
    }

    public String pushLocation (Location entity) {
        DatabaseReference ref = database.getReference (FirebaseReferences.LOCATIONS_REFERENCE).push ();
        ref.setValue (entity);
        return ref.getKey ();
    }

    public String pushReport (Report entity) {
        DatabaseReference ref = database.getReference (FirebaseReferences.REPORTS_REFERENCE).push ();
        ref.setValue (entity);
        return ref.getKey ();
    }

    public String pushStolenObject (StolenObject entity) {
        DatabaseReference ref = database.getReference (FirebaseReferences.STOLEN_OBJECTS_REFERENCE).push();
        ref.setValue (entity);
        return ref.getKey ();
    }

    public void pushFriendRequestOutToUser(String userID, User.FriendRequestOut friend){
        DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(userID)
                .child(FirebaseReferences.User.FRIEND_REQUESTS_OUT_REFERENCE).child(friend.getUser_id());
        ref.setValue(friend);
    }

}
