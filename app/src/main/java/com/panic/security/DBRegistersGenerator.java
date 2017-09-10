package com.panic.security;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.panic.security.firebase_utils.FirebaseReferences;

import java.util.ArrayList;
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

    private static String stolenObjects[] = {
            "Cycle", "Cellphone", "Car"
    };


    private static double latFrom = 4.5;
    private static double latTo = 4.75;
    private static double longFrom = -74.2;
    private static double longTo = -74;

    private static ArrayList<String> userIds = new ArrayList<>();

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static DatabaseReference generateRandomStolenObject () {
        DatabaseReference ref = database.getReference(FirebaseReferences.STOLEN_OBJECTS_REFERENCE);
        DatabaseReference childRef = ref.push();
        childRef.child(FirebaseReferences.StolenObject.DESCRIPTION_REFERENCE).setValue
                ("It was white and beauty, it had many gorgeous things.");
        childRef.child(FirebaseReferences.StolenObject.NAME_REFERENCE).setValue(stolenObjects[r.nextInt(stolenObjects.length)]);
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

    public static DatabaseReference generateRandomUser () {
        DatabaseReference ref = database.getReference (FirebaseReferences.USERS_REFERENCE);
        DatabaseReference childRef = ref.push ();
        childRef.child(FirebaseReferences.User.PROFILE_PICTURE_REFERENCE).setValue(childRef.getKey() + ".jpg");

        DatabaseReference profileRef = generateRandomProfile();

        childRef.child(FirebaseReferences.User.PROFILE_ID_REFERENCE).setValue(profileRef.getKey());
        childRef.child(FirebaseReferences.User.PHONE_NUMBER_REFERENCE).setValue("+57012345678" + r.nextInt(10));
        childRef.child(FirebaseReferences.User.EMAIL_REFERENCE).setValue(childRef.getKey().substring(
                Math.min(10, childRef.getKey().length()))
                + "@gmail.com");

        HashSet<String> friendsOutHash = new HashSet<>();

        DatabaseReference friendsOut = childRef.child(FirebaseReferences.User.FRIEND_REQUESTS_OUT_REFERENCE);

        int friendsOutNum = 0;
        if (!userIds.isEmpty()) {
            friendsOutNum = r.nextInt(userIds.size());
        }

        for (int i = 0; i < friendsOutNum; ++i) {
            String userID = userIds.get (r.nextInt (userIds.size()));
            friendsOutHash.add (userID);
        }

        for (String userId : friendsOutHash) {
            friendsOut.child(userId).child(FirebaseReferences.User.FriendRequestOut.DATE).setValue(ServerValue.TIMESTAMP);
            friendsOut.child(userId).child(FirebaseReferences.User.FriendRequestOut.USER_ID).setValue(userId);

            DatabaseReference friend = ref.child(userId).child(FirebaseReferences.User.FRIEND_REQUESTS_IN_REFERENCE).child(
                    childRef.getKey()
            );
            friend.child(FirebaseReferences.User.FriendRequestIn.USER_ID).setValue(childRef.getKey());
            friend.child(FirebaseReferences.User.FriendRequestIn.DATE).setValue(ServerValue.TIMESTAMP);
        }

        friendsOutNum *= 2;
        for (int i = 0; i < friendsOutNum; ++i) {
            String userId = userIds.get(r.nextInt(userIds.size()));
            if (!friendsOutHash.contains(userId)) {
                childRef.child(FirebaseReferences.User.FRIENDS_REFERENCE).child(userId).
                        child(FirebaseReferences.User.Friend.DATE).setValue(ServerValue.TIMESTAMP);
                childRef.child(FirebaseReferences.User.FRIENDS_REFERENCE).child(userId).
                        child(FirebaseReferences.User.Friend.IS_LOCATION_SHARED).setValue(false);
                childRef.child(FirebaseReferences.User.FRIENDS_REFERENCE).child(userId).
                        child(FirebaseReferences.User.Friend.USER_ID).setValue(userId);
            }
        }

        childRef.child(FirebaseReferences.User.IS_ACTIVE_REFERENCE).setValue(true);

        int reports = r.nextInt(11);

        for (int i = 0; i < reports; ++i) {
            DatabaseReference reportRef = generateRandomReport();
            childRef.child(FirebaseReferences.User.REPORTS_REFERENCE).child(reportRef.getKey()).setValue(reportRef.getKey());
        }

        userIds.add(childRef.getKey());
        return childRef;
    }

    public static void generateValues (final int VAL) {
        for (int i = 0; i < VAL; ++i) {
            generateRandomUser();
        }
    }
}
