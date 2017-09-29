package com.panic.security.firebase_utils;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    private FirebaseStorage storage;
    private CouchbaseDAO couchbaseDAO;

    private static FirebaseDAO firebaseDAO;
    private final long ONE_MEGABYTE = 1024 * 1024;

    private FirebaseDAO () {
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        couchbaseDAO = CouchbaseDAO.getInstance();
    }

    public static synchronized FirebaseDAO getInstance () {
        if (firebaseDAO == null) {
            firebaseDAO = new FirebaseDAO ();
        }
        return firebaseDAO;
    }

    public void getUserByID (String ID, final DataCallback<User> callback) {

        final DatabaseReference ref = database.getReference (FirebaseReferences.USERS_REFERENCE).child (ID);

        if (ID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            User user = couchbaseDAO.getUser();
            if (user != null) {
                callback.onDataReceive (user);
                return;
            }
            else {
                ref.addValueEventListener (new ValueEventListener() {
                    @Override
                    public void onDataChange (DataSnapshot dataSnapshot) {
                        User entity = dataSnapshot.getValue (User.class);
                        couchbaseDAO.pushUser (entity);
                    }

                    @Override
                    public void onCancelled (DatabaseError databaseError) {

                    }
                });
            }
        }

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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
        final DatabaseReference ref = database.getReference(FirebaseReferences.CRIMES_REFERENCE).child(ID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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
        final DatabaseReference ref = database.getReference(FirebaseReferences.LOCATIONS_REFERENCE).child(ID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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
        final DatabaseReference ref = database.getReference(FirebaseReferences.PROFILES_REFERENCE).child(ID);

        User user = couchbaseDAO.getUser();

        if (user != null) {
            if (user.getProfile_id ().equals (ID)) {
                Profile profile = couchbaseDAO.getProfile();
                if (profile != null) {
                    callback.onDataReceive(profile);
                }
                else {
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Profile entity = dataSnapshot.getValue (Profile.class);
                            couchbaseDAO.pushProfile (entity);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void getAllEmailsForAllUsers(final DataCallback< List<String> > callback) {
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> list = new ArrayList<>();
                Map<String, User> users = dataSnapshot.getValue(new GenericTypeIndicator<Map<String, User>>() {});
                for (Map.Entry<String, User> entry : users.entrySet()){
                    User user = entry.getValue();
                    list.add(user.getEmail());
                }
                callback.onDataReceive (list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getFriendsToUser(String userID, final DataCallback< HashMap<String, User.Friend> > callback){
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(userID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User entity = dataSnapshot.getValue (User.class);
                callback.onDataReceive (entity.getFriends());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });

    }

    public void getAllFullNamesForAllProfiles(final DataCallback< List<String> > callback) {
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.PROFILES_REFERENCE);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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
        final DatabaseReference ref = database.getReference(FirebaseReferences.REPORTS_REFERENCE).child(ID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void addCrimeLocationListener(final DataCallback<Pair<Crime, Location>> listener,
                                         final DataCallback<List<Pair<Crime, Location>>> callback) {
        final DatabaseReference ref = database.getReference(FirebaseReferences.CRIMES_REFERENCE);

        ref.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, Crime> crimes = dataSnapshot
                        .getValue(new GenericTypeIndicator<HashMap<String, Crime>>(){});
                final DatabaseReference locationsRef = database
                        .getReference(FirebaseReferences.LOCATIONS_REFERENCE);
                locationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Location> locations = dataSnapshot.getValue(
                                new GenericTypeIndicator<Map<String, Location>>() {}
                        );
                        List<Pair<Crime, Location>> pairs = new ArrayList<Pair<Crime, Location>>();
                        for (Map.Entry<String, Location> entry : locations.entrySet()) {
                            Location location = entry.getValue();
                            Crime crime = crimes.get (location.getCrime_id());
                            pairs.add (new Pair<>(crime, location));
                        }
                        callback.onDataReceive (pairs);
                        addCrimeLocationListener (listener);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void addCrimeLocationListener(final DataCallback<Pair<Crime, Location>> listener) {
        final DatabaseReference ref = database.getReference(FirebaseReferences.CRIMES_REFERENCE);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Crime crime = dataSnapshot.getValue (Crime.class);
                DatabaseReference locationRef = database.getReference(FirebaseReferences.LOCATIONS_REFERENCE)
                        .child(crime.getLocation_id());
                locationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Location location = dataSnapshot.getValue(Location.class);
                        listener.onDataReceive (new Pair<>(crime, location));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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

    public void getStolenObjectByID (String ID, final DataCallback<StolenObject> callback) {
        final DatabaseReference ref = database.getReference(FirebaseReferences.STOLEN_OBJECTS_REFERENCE).child(ID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void getUserByEmail (final String email, final DataCallback<User> callback){
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE);

        ref.orderByChild(FirebaseReferences.User.EMAIL_REFERENCE)
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<Map<String, User>> t = new GenericTypeIndicator<Map<String, User>>() {};
                        Map<String, User> map = dataSnapshot.getValue(t);

                        for (Map.Entry<String, User> entry : map.entrySet()) {
                            User user = entry.getValue();
                            callback.onDataReceive (user);
                        }
                        if (map.isEmpty()) {
                            callback.onDataReceive (null);
                        }

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

    public void areFriends(String currentUserID, String friendID, final DataCallback< User.Friend > callback) {
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(currentUserID).child(FirebaseReferences.User.FRIENDS_REFERENCE).child(friendID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void areFriendRequestOut(String currentUserID, String friendID, final DataCallback< User.FriendRequestOut > callback) {
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(currentUserID).child(FirebaseReferences.User.FRIEND_REQUESTS_OUT_REFERENCE).child(friendID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User.FriendRequestOut entity = dataSnapshot.getValue (User.FriendRequestOut.class);
                callback.onDataReceive (entity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void areFriendRequestIn(String currentUserID, String friendID, final DataCallback< User.FriendRequestIn> callback) {
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(currentUserID).child(FirebaseReferences.User.FRIEND_REQUESTS_IN_REFERENCE).child(friendID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User.FriendRequestIn entity = dataSnapshot.getValue (User.FriendRequestIn.class);
                callback.onDataReceive (entity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getProfileImageInBytes (String userID, final DataCallback<byte[]> callback) {
        StorageReference ref = storage.getReference(FirebaseReferences.PROFILE_PICTURES_FOLDER_REFERENCE).child(userID);
        ref.getBytes (ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes){
                callback.onDataReceive (bytes);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onDataReceive (null);
            }
        });

    }

    public String pushUser (String ID, User entity) {
        DatabaseReference ref = database.getReference (FirebaseReferences.USERS_REFERENCE).child (ID);
        entity.setId(ID);
        ref.setValue (entity);
        return ref.getKey ();
    }

    public String pushProfile ( String userId, Profile entity) {
        DatabaseReference ref = database.getReference (FirebaseReferences.PROFILES_REFERENCE).push ();
        entity.setId(ref.getKey());
        entity.setUser_id(userId);
        ref.setValue (entity);
        return ref.getKey ();
    }

    public String pushCrime (Crime entity) {
        DatabaseReference ref = database.getReference (FirebaseReferences.CRIMES_REFERENCE).push ();
        entity.setId(ref.getKey());
        ref.setValue (entity);
        return ref.getKey ();
    }

    public String pushLocation (Location entity) {
        DatabaseReference ref = database.getReference (FirebaseReferences.LOCATIONS_REFERENCE).push ();
        entity.setId(ref.getKey());
        ref.setValue (entity);
        return ref.getKey ();
    }

    public String pushReport (Report entity) {
        DatabaseReference ref = database.getReference (FirebaseReferences.REPORTS_REFERENCE).push ();
        entity.setId(ref.getKey());
        ref.setValue (entity);
        return ref.getKey ();
    }

    public String pushStolenObject (StolenObject entity) {
        DatabaseReference ref = database.getReference (FirebaseReferences.STOLEN_OBJECTS_REFERENCE).push();
        entity.setId(ref.getKey());
        ref.setValue (entity);
        return ref.getKey ();
    }

    public void pushFriend(User.Friend friend){
        DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(friend.getUser_id())
                .child(FirebaseReferences.User.FRIENDS_REFERENCE).child(friend.getFriend_id());
        ref.setValue(friend);
    }

    public void pushFriendRequestOut(User.FriendRequestOut friendRequestOut){
        DatabaseReference refOut = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(friendRequestOut.getUser_id())
                .child(FirebaseReferences.User.FRIEND_REQUESTS_OUT_REFERENCE).child(friendRequestOut.getFriend_id());
        refOut.setValue(friendRequestOut);
    }

    public void pushFriendRequestIn(User.FriendRequestIn friendRequestIn){
        DatabaseReference refIn = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(friendRequestIn.getUser_id())
                .child(FirebaseReferences.User.FRIEND_REQUESTS_IN_REFERENCE).child(friendRequestIn.getFriend_id());
        refIn.setValue(friendRequestIn);
    }

    public void removeFriendRequestOut(User.FriendRequestOut friendRequestOut){
        DatabaseReference refOut = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(friendRequestOut.getUser_id())
                .child(FirebaseReferences.User.FRIEND_REQUESTS_OUT_REFERENCE).child(friendRequestOut.getFriend_id());
        refOut.removeValue();
    }

    public void removeFriendRequestIn(User.FriendRequestIn friendRequestIn){
        DatabaseReference refIn = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(friendRequestIn.getUser_id())
                .child(FirebaseReferences.User.FRIEND_REQUESTS_IN_REFERENCE).child(friendRequestIn.getFriend_id());
        refIn.removeValue();
    }


}
