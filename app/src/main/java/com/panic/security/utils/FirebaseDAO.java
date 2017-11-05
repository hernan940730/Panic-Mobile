package com.panic.security.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.entities.Crime;
import com.panic.security.entities.Friend;
import com.panic.security.entities.FriendRequest;
import com.panic.security.entities.Location;
import com.panic.security.entities.Profile;
import com.panic.security.entities.Report;
import com.panic.security.entities.StolenObject;
import com.panic.security.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            if (!couchbaseDAO.getIsUserListenerSet()) {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User entity = dataSnapshot.getValue(User.class);
                        couchbaseDAO.pushUser (entity);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            if (!DataLoader.getInstance().hasActiveInternetConnection()){
                User user = couchbaseDAO.getUser();
                if (user != null) {
                    callback.onDataReceive(user);
                    return;
                }
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

    public void getProfileByID (String ID, final DataCallback<Profile> callback) {

        final DatabaseReference ref = database.getReference(FirebaseReferences.PROFILES_REFERENCE).child(ID);

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals (ID)) {

            if (!couchbaseDAO.getIsProfileListenerSet()) {
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

            if (!DataLoader.getInstance().hasActiveInternetConnection()) {
                Profile profile = couchbaseDAO.getProfile();
                if (profile != null) {
                    callback.onDataReceive(profile);
                    return;
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

    public ValueEventListener addUserListener(String userID, final DataCallback< User > listener){

        final DatabaseReference userRef = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(userID);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                listener.onDataReceive(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        userRef.addListenerForSingleValueEvent(valueEventListener);
        return valueEventListener;
    }

    public void detachUserListener(String userID, ValueEventListener listener){
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(userID);
        ref.removeEventListener(listener);
    }

    public ValueEventListener addProfileListener(String profileID, final DataCallback< Profile > listener){

        final DatabaseReference userRef = database.getReference().child(FirebaseReferences.PROFILES_REFERENCE).child(profileID);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Profile profile = dataSnapshot.getValue(Profile.class);
                listener.onDataReceive(profile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        userRef.addListenerForSingleValueEvent(valueEventListener);
        return valueEventListener;
    }

    public void detachProfileListener(String profileID, ValueEventListener listener){
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE).child(profileID);
        ref.removeEventListener(listener);
    }

    public void getAllEmailsForAllUsers(final DataCallback< List<String> > callback) {
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE);

        ref.addValueEventListener(new ValueEventListener() {
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

    public void getUserFriends(String userId, final DataCallback< Map<String, Friend> > callback) {
        final DatabaseReference ref = database.getReference()
                .child(FirebaseReferences.USER_FRIENDS_REFERENCE)
                .child(userId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Friend> friends = dataSnapshot.getValue(
                        new GenericTypeIndicator<Map<String, Friend>>() {}
                );
                callback.onDataReceive (friends);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });

    }

    public ChildEventListener addUserFriendsChildListener(String userId, final DataCallback<Friend> callback ) {
        final DatabaseReference ref = database.getReference()
                .child(FirebaseReferences.USER_FRIENDS_REFERENCE)
                .child(userId);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Friend friend = dataSnapshot.getValue(Friend.class);
                callback.onDataReceive(friend);
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
        };

        ref.addChildEventListener(listener);

        return listener;
    }

    public void revokeUserFriendsChildListener(String userId, ChildEventListener listener ) {
        DatabaseReference ref = database.getReference()
                .child(FirebaseReferences.USER_FRIENDS_REFERENCE)
                .child(userId);
        ref.removeEventListener(listener);
    }

    public void getAllFullNamesForAllProfiles(final DataCallback< List<String> > callback) {
        final DatabaseReference ref = database.getReference().child(FirebaseReferences.PROFILES_REFERENCE);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> list = new ArrayList<>();
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

                        if(map == null){
                            callback.onDataReceive (null);
                        }else{
                            for (Map.Entry<String, User> entry : map.entrySet()) {
                                User user = entry.getValue();
                                callback.onDataReceive (user);
                            }
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

    public ValueEventListener getUserReports (final String userId, final DataCallback<Map<String, String>> callback) {
        final DatabaseReference ref = database.getReference()
                .child(FirebaseReferences.USER_REPORTS_REFERENCE)
                .child(userId);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> reports = dataSnapshot.getValue(
                        new GenericTypeIndicator<Map<String, String>>() {}
                );
                callback.onDataReceive(reports);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        };
        ref.addListenerForSingleValueEvent(listener);
        return listener;
    }

    public void revokeUserReportsListener(String userId, ValueEventListener listener) {
        DatabaseReference ref = database.getReference()
                .child(FirebaseReferences.USER_REPORTS_REFERENCE)
                .child(userId);
        ref.removeEventListener(listener);
    }

    public void getUserFriendRequestsIn (final DataCallback<Map<String, FriendRequest>> callback) {
        final String currUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference ref = database.getReference()
                .child(FirebaseReferences.USER_FRIEND_REQUESTS_IN_REFERENCE)
                .child(currUserId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, FriendRequest> reports = dataSnapshot.getValue(
                        new GenericTypeIndicator<Map<String, FriendRequest>>() {}
                );
                callback.onDataReceive(reports);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public ChildEventListener addUserFriendRequestsInChildListener(final DataCallback<FriendRequest> callback) {
        final String currUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference ref = database.getReference()
                .child(FirebaseReferences.USER_FRIEND_REQUESTS_IN_REFERENCE)
                .child(currUserId);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FriendRequest friendRequest = dataSnapshot.getValue(FriendRequest.class);
                callback.onDataReceive(friendRequest);
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
        };

        ref.addChildEventListener(listener);

        return listener;
    }

    public void revokeUserFriendRequestsInChildListener(ChildEventListener listener){
        final String currUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = database.getReference()
                .child(FirebaseReferences.USER_FRIEND_REQUESTS_IN_REFERENCE)
                .child(currUserId);
        ref.removeEventListener(listener);
    }

    public void areFriends(String currentUserID, String friendID, final DataCallback<Friend> callback) {
        final DatabaseReference ref = database.getReference()
                .child(FirebaseReferences.USER_FRIENDS_REFERENCE)
                .child(currentUserID)
                .child(friendID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Friend entity = dataSnapshot.getValue (Friend.class);
                callback.onDataReceive (entity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void areFriendRequestOut(String currentUserID, String friendID, final DataCallback<com.panic.security.entities.FriendRequest> callback) {
        final DatabaseReference ref = database.getReference()
                .child(FirebaseReferences.USER_FRIEND_REQUESTS_OUT_REFERENCE)
                .child(currentUserID)
                .child(friendID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                com.panic.security.entities.FriendRequest entity = dataSnapshot.getValue (com.panic.security.entities.FriendRequest.class);
                callback.onDataReceive (entity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void areFriendRequestIn(String currentUserID, String friendID, final DataCallback<com.panic.security.entities.FriendRequest> callback) {
        final DatabaseReference ref = database.getReference()
                .child(FirebaseReferences.USER_FRIEND_REQUESTS_IN_REFERENCE)
                .child(currentUserID)
                .child(friendID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                com.panic.security.entities.FriendRequest entity = dataSnapshot.getValue (com.panic.security.entities.FriendRequest.class);
                callback.onDataReceive (entity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void putProfileImageInView(String userID, final Activity activity, final ImageView view) {
        final StorageReference ref = storage.getReference(FirebaseReferences.PROFILE_PICTURES_FOLDER_REFERENCE).child(userID);
        ref.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                GlideApp.with(activity)
                        .load(ref)
                        .override(100)
                        .centerCrop()
                        .placeholder(R.drawable.ic_default_profile_image)
                        .signature(new ObjectKey(storageMetadata.getUpdatedTimeMillis()))
                        .into(view);
            }
        });
    }

    public void putRoundProfileImageInView(String userID, final Activity activity, final ImageView view) {
        final StorageReference ref = storage.getReference(FirebaseReferences.PROFILE_PICTURES_FOLDER_REFERENCE).child(userID);
        ref.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                GlideApp.with(activity)
                        .load(ref)
                        .override(300)
                        .circleCrop()
                        .placeholder(R.drawable.ic_default_profile_image)
                        .signature(new ObjectKey(storageMetadata.getUpdatedTimeMillis()))
                        .into(view);
            }
        });
    }

    public void putFullProfileImageInView(String userID, final Activity activity, final ImageView view) {
        final StorageReference ref = storage.getReference(FirebaseReferences.PROFILE_PICTURES_FOLDER_REFERENCE).child(userID);
        ref.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                GlideApp.with(activity)
                        .load(ref)
                        .signature(new ObjectKey(storageMetadata.getUpdatedTimeMillis()))
                        .into(view);
            }
        });
    }

    public void getProfileImageInBytes(String userID, final DataCallback<byte[]> callback) {
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

    public void pushProfileImage(String userId, Uri uri) {
        StorageReference ref = storage.getReference(FirebaseReferences.PROFILE_PICTURES_FOLDER_REFERENCE).child(userId);
        ref.putFile(uri);
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

    public String pushReport (Report report, Crime crime, Location location) {
        DatabaseReference reportRef = database.getReference (FirebaseReferences.REPORTS_REFERENCE).push ();
        DatabaseReference crimeRef = database.getReference (FirebaseReferences.CRIMES_REFERENCE).push ();
        DatabaseReference locationRef = database.getReference (FirebaseReferences.LOCATIONS_REFERENCE).push ();

        location.setCrime_id (crimeRef.getKey());
        location.setId (locationRef.getKey());

        crime.setId (crimeRef.getKey());
        crime.setReport_id (reportRef.getKey());
        crime.setLocation_id (locationRef.getKey());

        report.setId (reportRef.getKey());
        report.setCrime_id (crimeRef.getKey());
        report.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        locationRef.setValue (location);
        crimeRef.setValue (crime);
        reportRef.setValue (report);
        reportRef.child(FirebaseReferences.Report.DATE_REFERENCE).setValue(ServerValue.TIMESTAMP);

        database.getReference (FirebaseReferences.USER_REPORTS_REFERENCE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(report.getId())
                .setValue(report.getId());

        return reportRef.getKey ();
    }

    public String pushStolenObject (StolenObject entity) {
        DatabaseReference ref = database.getReference (FirebaseReferences.STOLEN_OBJECTS_REFERENCE).push();
        entity.setId(ref.getKey());
        ref.setValue (entity);
        return ref.getKey ();
    }

    public void pushFriend(String userId2){
        final String userId1 = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref1 = database.getReference (FirebaseReferences.USER_FRIENDS_REFERENCE)
                .child(userId1)
                .child(userId2);

        DatabaseReference ref2 = database.getReference (FirebaseReferences.USER_FRIENDS_REFERENCE)
                .child(userId2)
                .child(userId1);

        Friend friend1 = new Friend(
                userId1,
                0,
                false
        );

        Friend friend2 = new Friend(
                userId2,
                0,
                false
        );

        ref1.setValue(friend2);
        ref2.setValue(friend1);
        ref1.child(FirebaseReferences.Friend.DATE).setValue(ServerValue.TIMESTAMP);
        ref2.child(FirebaseReferences.Friend.DATE).setValue(ServerValue.TIMESTAMP);
    }

    public void pushFriendRequest (final String friendId) {
        final String currUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference refIn = database.getReference()
                .child(FirebaseReferences.USER_FRIEND_REQUESTS_IN_REFERENCE)
                .child(friendId)
                .child(currUserId);

        DatabaseReference refOut = database.getReference()
                .child(FirebaseReferences.USER_FRIEND_REQUESTS_OUT_REFERENCE)
                .child(currUserId)
                .child(friendId);

        com.panic.security.entities.FriendRequest friendRequestOut = new com.panic.security.entities.FriendRequest(
                friendId,
                0
        );

        com.panic.security.entities.FriendRequest friendRequestIn = new com.panic.security.entities.FriendRequest(
                currUserId,
                0
        );

        refIn.setValue(friendRequestIn);
        refOut.setValue(friendRequestOut);

        refIn.child(FirebaseReferences.FriendRequest.DATE).setValue(ServerValue.TIMESTAMP);
        refOut.child(FirebaseReferences.FriendRequest.DATE).setValue(ServerValue.TIMESTAMP);
    }

    public void removeFriendRequest (final String friendId) {
        final String currUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference refIn = database.getReference()
                .child(FirebaseReferences.USER_FRIEND_REQUESTS_IN_REFERENCE)
                .child(currUserId)
                .child(friendId);

        DatabaseReference refOut = database.getReference()
                .child(FirebaseReferences.USER_FRIEND_REQUESTS_OUT_REFERENCE)
                .child(friendId)
                .child(currUserId);

        refIn.removeValue();
        refOut.removeValue();
    }

    public void removeFriend(String friendId) {
        final String currUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference refMyFriend = database.getReference()
                .child(FirebaseReferences.USER_FRIENDS_REFERENCE)
                .child(currUserId)
                .child(friendId);

        DatabaseReference refFriend = database.getReference()
                .child(FirebaseReferences.USER_FRIENDS_REFERENCE)
                .child(friendId)
                .child(currUserId);

        refMyFriend.removeValue();
        refFriend.removeValue();
    }

    public void sendLocation(Set<String> friends) {
        DatabaseReference refSharing = database.getReference()
                .child(FirebaseReferences.USER_FRIENDS_SHARING_REFERENCE);
        DatabaseReference refShared = database.getReference()
                .child(FirebaseReferences.USER_FRIENDS_SHARED_REFERENCE);
        final String userId = FirebaseAuth.getInstance().getUid();
        for (String friendId : friends) {
            refSharing.child(friendId).child(userId).setValue(userId);
            refShared.child(userId).child(friendId).setValue(friendId);
        }
    }

    public void revokeSendLocation() {
        final String userId = FirebaseAuth.getInstance().getUid();
        final DatabaseReference refSharing = database.getReference()
                .child(FirebaseReferences.USER_FRIENDS_SHARING_REFERENCE);
        final DatabaseReference refShared = database.getReference()
                .child(FirebaseReferences.USER_FRIENDS_SHARED_REFERENCE)
                .child(userId);

        refShared.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, String> friendIds = dataSnapshot
                        .getValue(new GenericTypeIndicator<HashMap<String, String>>(){});

                if (friendIds != null) {
                    for (Map.Entry<String, String> friendId : friendIds.entrySet()) {
                        refSharing.child(friendId.getValue()).child(userId).removeValue();
                    }
                }
                refShared.removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
