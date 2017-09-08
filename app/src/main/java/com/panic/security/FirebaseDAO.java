package com.panic.security;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.panic.security.entities.User;

import org.json.JSONObject;

/**
 * Created by david on 9/7/17.
 */

public class FirebaseDAO {
    private FirebaseDatabase database;

    private User user;

    public FirebaseDAO () {
        database = FirebaseDatabase.getInstance();
    }

    public User getUserByUID (String UID) {
        DatabaseReference userRef = database.getReference(FirebaseReferences.USERS_REFERENCE).child(UID);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                System.out.println( "email::" + dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return user;
    }
}
