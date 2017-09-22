package com.panic.security.firebase_utils;

import com.google.firebase.database.ChildEventListener;
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
        final DatabaseReference ref = database.getReference(FirebaseReferences.USERS_REFERENCE).child(ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User entity = dataSnapshot.getValue (User.class);
                entity.setKey(dataSnapshot.getKey());
                callback.onDataReceive (entity);
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getCrimeByID (String ID, final DataCallback<Crime> callback) {
        final DatabaseReference ref = database.getReference(FirebaseReferences.CRIMES_REFERENCE).child(ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Crime entity = dataSnapshot.getValue (Crime.class);
                callback.onDataReceive (entity);
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getLocationByID (String ID, final DataCallback<Location> callback) {
        final DatabaseReference ref = database.getReference(FirebaseReferences.LOCATIONS_REFERENCE).child(ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Location entity = dataSnapshot.getValue (Location.class);
                callback.onDataReceive (entity);
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getProfileByID (String ID, final DataCallback<Profile> callback) {
        final DatabaseReference ref = database.getReference(FirebaseReferences.PROFILES_REFERENCE).child(ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile entity = dataSnapshot.getValue (Profile.class);
                callback.onDataReceive (entity);
                //ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getAllEmailsForAllUsers(final DataCallback< List<String> > callback) {
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> list = new ArrayList<String>();
                Map<String,Object> users =  (Map<String,Object>) dataSnapshot.getValue();
                for (Map.Entry<String, Object> entry : users.entrySet()){
                    Map singleProfile = (Map) entry.getValue();
                    list.add((String)singleProfile.get(FirebaseReferences.User.EMAIL_REFERENCE));
                }
                callback.onDataReceive (list);
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }


    public void getAllFullNamesForAllProfiles(final DataCallback< List<String> > callback) {
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.PROFILES_REFERENCE);

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
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getReportByID (String ID, final DataCallback<Report> callback) {
        final DatabaseReference ref = database.getReference(FirebaseReferences.REPORTS_REFERENCE).child(ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Report entity = dataSnapshot.getValue (Report.class);
                callback.onDataReceive (entity);
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getStolenObjectByID (String ID, final DataCallback<StolenObject> callback) {
        final DatabaseReference ref = database.getReference(FirebaseReferences.STOLEN_OBJECTS_REFERENCE).child(ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StolenObject entity = dataSnapshot.getValue (StolenObject.class);
                callback.onDataReceive (entity);
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getUserByEmail(final String email, final DataCallback<User> callback ){

        final DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                User user = dataSnapshot.getValue(User.class);
                user.setKey(dataSnapshot.getKey());
                if(user.getEmail().equals( email )){
                    callback.onDataReceive(user);
                    ref.removeEventListener(this);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getUserIDByProfileID(final String profileID, final DataCallback<String> callback) {

        final DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                User user = dataSnapshot.getValue(User.class);
                if(user.getProfile_id().equals(profileID)){
                    callback.onDataReceive(dataSnapshot.getKey());
                    ref.removeEventListener(this);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void areFriends(String currentUserID, String userID, final DataCallback< User.Friend > callback) {
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(currentUserID).child(FirebaseReferences.User.FRIENDS_REFERENCE).child(userID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User.Friend entity = dataSnapshot.getValue (User.Friend.class);
                callback.onDataReceive (entity);
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void areFriendRequestOut(String currentUserID, String userID, final DataCallback< User.FriendRequestOut > callback) {
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(currentUserID).child(FirebaseReferences.User.FRIEND_REQUESTS_OUT_REFERENCE).child(userID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User.FriendRequestOut entity = dataSnapshot.getValue (User.FriendRequestOut.class);
                callback.onDataReceive (entity);
                ref.removeEventListener(this);
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
