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

    public void getAllFullNamesForAllUsers(final DataCallback< List<String> > callback) {

        DatabaseReference ref = database.getReference().child(FirebaseReferences.USERS_REFERENCE);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final List<String> list = new ArrayList<String>();

                Map<String,Object> users =  (Map<String,Object>) dataSnapshot.getValue();
                //iterate through each user, ignoring their UID
                for (Map.Entry<String, Object> entry : users.entrySet()){

                    //Get user map
                    Map singleUser = (Map) entry.getValue();
                    String idProfileToSingleUser = (String)(singleUser.get(FirebaseReferences.User.PROFILE_ID_REFERENCE));

                    DatabaseReference ref = database.getReference(FirebaseReferences.PROFILES_REFERENCE).child(idProfileToSingleUser);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Profile entity = dataSnapshot.getValue (Profile.class);
                            list.add(entity.getName() + " " + entity.getLast_name());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            callback.onDataReceive (null);
                        }
                    });

                }

                callback.onDataReceive (list);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
    }

    public void getAllProfiles(final DataCallback< Map<String,Object> > callback) {
        DatabaseReference ref = database.getReference().child(FirebaseReferences.PROFILES_REFERENCE);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> map =  (Map<String,Object>) dataSnapshot.getValue();
                callback.onDataReceive (map);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceive (null);
            }
        });
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
        DatabaseReference ref = database.getReference (FirebaseReferences.STOLEN_OBJECTS_REFERENCE).push ();
        ref.setValue (entity);
        return ref.getKey ();
    }

}
