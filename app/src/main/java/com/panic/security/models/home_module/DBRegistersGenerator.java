package com.panic.security.models.home_module;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.panic.security.FirebaseReferences;
import com.panic.security.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by david on 9/2/17.
 */

public abstract class DBRegistersGenerator {

    private static Random r = new Random();

    private static String gender[] = {
            "male_gender", "female_gender"
    };

    private static String crimeList[] = {
            "assault_crime",
            "auto_theft_crime",
            "burglary_crime",
            "shop_lifting_crime",
            "suspicious_activity_crime",
            "homicide_crime",
            "vandalism_crime",
            "drugs_crime",
            "other_crime"
    };

    private static String lastnamesList[] = {
            "Tu",
            "Zylstra",
            "Wyss",
            "Tejera",
            "Hiles",
            "Ferrari",
            "Cromer",
            "Sera",
            "Hollon",
            "Craney",
            "Sacco",
            "Monterroso",
            "Brune",
            "Leamon",
            "Yeary",
            "Heilman",
            "Keating",
            "Boateng",
            "Bolduc",
            "Layman",
            "Phong",
            "Beltran",
            "Zamora",
            "Smallwood",
            "Rusek",
            "Sayers",
            "Sparr",
            "Eddie",
            "Beauchesne",
            "Isett",
            "Saliba",
            "Pigg",
            "Duran",
            "Lossett",
            "Donaghy",
            "Fuson",
            "Garriott",
            "Rowton",
            "Carvajal",
            "Bake",
            "Rieder",
            "Deshields",
            "Mathes",
            "Yeadon",
            "Bessler",
            "Jesse",
            "Liska",
            "Velazquez",
            "Crist",
            "Parkins"
    };

    private static String namesList[] = {
            "Bunny",
            "Sherlyn",
            "Sheryl",
            "Pearle",
            "Hilda",
            "Audrie",
            "Nannette",
            "Chan",
            "Minnie",
            "Ebony",
            "Isabelle",
            "Gala",
            "Emanuel",
            "India",
            "Sofia",
            "Brady",
            "Cesar",
            "Carmelia",
            "Belle",
            "Kraig",
            "Theron",
            "Hermila",
            "Kasey",
            "Letisha",
            "Stephania",
            "Regine",
            "Armando",
            "Rex",
            "Allan",
            "Mechelle",
            "Luise",
            "Pearl",
            "Waltraud",
            "Kendal",
            "Isaias",
            "Kiersten",
            "Cathy",
            "Drew",
            "Charline",
            "Angeles",
            "Devora",
            "Norma",
            "Rosendo",
            "Clark",
            "Hiram",
            "Sammie",
            "Bernetta",
            "Launa",
            "Lupita",
            "Somer"
    };

    private static double latFrom = 4.5;
    private static double latTo = 4.75;
    private static double longFrom = -74.2;
    private static double longTo = -74;

    private static ArrayList<String> userIds;

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void generateRandomCrime () {
        DatabaseReference ref = database.getReference(FirebaseReferences.CRIMES_REFERENCE);
        DatabaseReference childRef = ref.push();
        childRef.child(FirebaseReferences.Crime.TYPE_REFERENCE).setValue(crimeList[r.nextInt(crimeList.length)]);
        childRef.child(FirebaseReferences.Crime.DATE_REFERENCE).setValue(ServerValue.TIMESTAMP);
    }

    public static void generateRandomLocation () {
        DatabaseReference ref = database.getReference(FirebaseReferences.LOCATIONS_REFERENCE);
        DatabaseReference childRef = ref.push();
        childRef.child(FirebaseReferences.Location.LATITUDE_REFERENCE).setValue(
                r.nextDouble() * (latTo - latFrom) + latFrom);
        childRef.child(FirebaseReferences.Location.LONGITUDE_REFERENCE).setValue(
                r.nextDouble() * (longTo - longFrom) + longFrom);
    }

    public static void generateRandomProfile () {
        DatabaseReference ref = database.getReference(FirebaseReferences.PROFILES_REFERENCE);
        DatabaseReference childRef = ref.push();
        childRef.child(FirebaseReferences.Profile.BIRTHDAY_REFERENCE).setValue(ServerValue.TIMESTAMP);
        childRef.child(FirebaseReferences.Profile.GENDER_REFERENCE).setValue(gender[r.nextInt(gender.length)]);
        childRef.child(FirebaseReferences.Profile.LASTNAME_REFERENCE).setValue(
                lastnamesList[r.nextInt(lastnamesList.length)]);
        childRef.child(FirebaseReferences.Profile.NAME_REFERENCE).setValue(
                namesList[r.nextInt(namesList.length)]);
    }

    public static void fillUsers () {

    }

    public static void generateUsers () {
        final DatabaseReference profilesRef = database.getReference(FirebaseReferences.PROFILES_REFERENCE);
        final DatabaseReference usersRef = database.getReference(FirebaseReferences.USERS_REFERENCE);

        profilesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String lastname = dataSnapshot.child(FirebaseReferences.Profile.LASTNAME_REFERENCE).getValue(String.class);
                String name = dataSnapshot.child(FirebaseReferences.Profile.NAME_REFERENCE).getValue(String.class);

                String id = dataSnapshot.getKey();
                String email = name + "_" + lastname + "@gmail.com";
                DatabaseReference childRef = usersRef.push();
                childRef.child(FirebaseReferences.User.EMAIL_REFERENCE).setValue(email);
                childRef.child(FirebaseReferences.User.IS_ACTIVE_REFERENCE).setValue(true);
                childRef.child(FirebaseReferences.User.PASSWORD_REFERENCE).setValue("1234");
                childRef.child(FirebaseReferences.User.PHONE_NUMBER_REFERENCE).setValue("+57-1234567890");
                childRef.child(FirebaseReferences.User.PROFILE_ID_REFERENCE).setValue(id);
                childRef.child(FirebaseReferences.User.PROFILE_PICTURE_REFERENCE).setValue("/dir/photo");

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

    public static void generateValues (final String REF_NAME, final String FIELDS[], final int SIZE) {
        DatabaseReference ref = database.getReference (REF_NAME);
        for (int i = 0; i < SIZE; ++i) {
            DatabaseReference child = ref.push();

        }
    }
}
