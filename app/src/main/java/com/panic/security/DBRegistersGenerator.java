package com.panic.security;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
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

    private static String stolenObjects[] = {
            "Cycle", "Cellphone", "Car"
    };


    private static double latFrom = 4.5;
    private static double latTo = 4.75;
    private static double longFrom = -74.2;
    private static double longTo = -74;

    private static ArrayList<String> userIds;

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static DatabaseReference generateRandomStolenObject () {
        DatabaseReference ref = database.getReference(FirebaseReferences.STOLEN_OBJECTS_REFERENCE);
        DatabaseReference childRef = ref.push();
        childRef.child(FirebaseReferences.StolenObject.DESCRIPTION_REFERENCE).setValue
                ("It was white and beauty, it had many gorgeous things.");
        childRef.child(FirebaseReferences.StolenObject.NAME_REFERENCE).setValue(ServerValue.TIMESTAMP);
        return childRef;
    }

    public static DatabaseReference generateRandomCrime () {
        DatabaseReference ref = database.getReference(FirebaseReferences.CRIMES_REFERENCE);
        DatabaseReference childRef = ref.push();
        childRef.child(FirebaseReferences.Crime.TYPE_REFERENCE).setValue(crimeList[r.nextInt(crimeList.length)]);
        childRef.child(FirebaseReferences.Crime.DATE_REFERENCE).setValue(ServerValue.TIMESTAMP);
        return childRef;
    }

    public static DatabaseReference generateRandomLocation () {
        DatabaseReference ref = database.getReference(FirebaseReferences.LOCATIONS_REFERENCE);
        DatabaseReference childRef = ref.push();
        childRef.child(FirebaseReferences.Location.LATITUDE_REFERENCE).setValue(
                r.nextDouble() * (latTo - latFrom) + latFrom);
        childRef.child(FirebaseReferences.Location.LONGITUDE_REFERENCE).setValue(
                r.nextDouble() * (longTo - longFrom) + longFrom);
        return childRef;
    }

    public static DatabaseReference generateRandomProfile () {
        DatabaseReference ref = database.getReference(FirebaseReferences.PROFILES_REFERENCE);
        DatabaseReference childRef = ref.push();
        childRef.child(FirebaseReferences.Profile.BIRTHDAY_REFERENCE).setValue(ServerValue.TIMESTAMP);
        childRef.child(FirebaseReferences.Profile.GENDER_REFERENCE).setValue(gender[r.nextInt(gender.length)]);
        childRef.child(FirebaseReferences.Profile.LAST_NAME_REFERENCE).setValue(
                lastnamesList[r.nextInt(lastnamesList.length)]);
        childRef.child(FirebaseReferences.Profile.NAME_REFERENCE).setValue(
                namesList[r.nextInt(namesList.length)]);
        return childRef;
    }

    public static DatabaseReference generateRandomReport () {
        DatabaseReference ref = database.getReference(FirebaseReferences.REPORTS_REFERENCE);
        DatabaseReference childRef = ref.push();
        DatabaseReference crimeRef = generateRandomCrime();
        DatabaseReference locationRef = generateRandomLocation();

        childRef.child(FirebaseReferences.Report.DATE_REFERENCE).setValue(ServerValue.TIMESTAMP);
        childRef.child(FirebaseReferences.Report.DESCRIPTION_REFERENCE).setValue(
                "I was in the street and I don't know what more I can say, " +
                        "just that it was a very tragic moment in my life and I'm very sorry for this.");

        childRef.child(FirebaseReferences.Report.CRIME_ID_REFERENCE).setValue(crimeRef.getKey());
        childRef.child(FirebaseReferences.Report.LOCATION_ID_REFERENCE).setValue(locationRef.getKey());

        final int SIZE = r.nextInt(4);
        for (int i = 0; i < SIZE; ++i) {
            DatabaseReference stolenObjectRef = generateRandomStolenObject();
            childRef.child(FirebaseReferences.Report.STOLEN_OBJECTS_REFERENCE).child(stolenObjectRef.getKey()).setValue(stolenObjectRef.getKey());
        }


        return childRef;
    }

    public static void generateUsers () {
        final DatabaseReference profilesRef = database.getReference(FirebaseReferences.PROFILES_REFERENCE);
        final DatabaseReference usersRef = database.getReference(FirebaseReferences.USERS_REFERENCE);

        profilesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String lastname = dataSnapshot.child(FirebaseReferences.Profile.LAST_NAME_REFERENCE).getValue(String.class);
                String name = dataSnapshot.child(FirebaseReferences.Profile.NAME_REFERENCE).getValue(String.class);

                String id = dataSnapshot.getKey();
                String email = name + "_" + lastname + "@gmail.com";
                DatabaseReference childRef = usersRef.push();
                childRef.child(FirebaseReferences.User.EMAIL_REFERENCE).setValue(email);
                childRef.child(FirebaseReferences.User.IS_ACTIVE_REFERENCE).setValue(true);
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

    public static void generateValues () {

    }
}
