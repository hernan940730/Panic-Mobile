package com.panic.security.models.home_module;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.panic.security.FirebaseReferences;
import com.panic.security.R;

/**
 * Created by david on 9/2/17.
 */

public class Test {

    private String crimeList[] = {};
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public void generateRandomCrime () {

    }

    public void generateValues (final String REF_NAME, final String FIELDS[], final int SIZE) {
        DatabaseReference ref = database.getReference (REF_NAME);
        for (int i = 0; i < SIZE; ++i) {
            DatabaseReference child = ref.push();

        }
    }
}
