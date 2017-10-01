package com.panic.security;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.panic.security.entities.Crime;
import com.panic.security.entities.Location;
import com.panic.security.entities.Profile;
import com.panic.security.entities.Report;
import com.panic.security.entities.StolenObject;
import com.panic.security.entities.User;
import com.panic.security.utils.FirebaseReferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

    public static DatabaseReference generateRandomStolenObject (String reportId) {
        DatabaseReference ref = database.getReference(FirebaseReferences.STOLEN_OBJECTS_REFERENCE);
        DatabaseReference childRef = ref.push();

        StolenObject stolenObject = new StolenObject();

        stolenObject.setDescription ("It was white and beauty, it had many gorgeous things.");
        stolenObject.setId (childRef.getKey());
        stolenObject.setName (stolenObjects[r.nextInt(stolenObjects.length)]);
        stolenObject.setReport_id(reportId);

        childRef.setValue (stolenObject);

        return childRef;
    }

    public static DatabaseReference generateRandomCrime (String reportId) {
        DatabaseReference ref = database.getReference(FirebaseReferences.CRIMES_REFERENCE);
        DatabaseReference childRef = ref.push();

        DatabaseReference location = generateRandomLocation(childRef.getKey());

        Crime crime = new Crime();

        crime.setReport_id (reportId);
        crime.setId (childRef.getKey());
        crime.setDate (0);
        crime.setLocation_id(location.getKey());
        crime.setType(crimeList[r.nextInt(crimeList.length)]);

        childRef.setValue (crime);

        return childRef;
    }

    public static DatabaseReference generateRandomLocation (String crimeId) {
        DatabaseReference ref = database.getReference(FirebaseReferences.LOCATIONS_REFERENCE);
        DatabaseReference childRef = ref.push();

        Location location = new Location();

        location.setId (childRef.getKey());
        location.setCrime_id (crimeId);
        location.setLatitude (r.nextDouble() * (latTo - latFrom) + latFrom);
        location.setLongitude(r.nextDouble() * (longTo - longFrom) + longFrom);

        childRef.setValue(location);

        return childRef;
    }

    public static DatabaseReference generateRandomProfile (String userId) {
        DatabaseReference ref = database.getReference(FirebaseReferences.PROFILES_REFERENCE);
        DatabaseReference childRef = ref.push();

        Profile profile = new Profile();

        profile.setBirthday(0);
        profile.setLast_name(lastnamesList[r.nextInt(lastnamesList.length)]);
        profile.setGender(gender[r.nextInt(gender.length)]);
        profile.setCountry("colombia");
        profile.setId(childRef.getKey());
        profile.setName(namesList[r.nextInt(namesList.length)]);
        profile.setUser_id(userId);

        childRef.setValue (profile);

        return childRef;
    }

    public static DatabaseReference generateRandomReport () {
        DatabaseReference ref = database.getReference(FirebaseReferences.REPORTS_REFERENCE);
        DatabaseReference childRef = ref.push();
        DatabaseReference crimeRef = generateRandomCrime(childRef.getKey());

        Map<String, String> stolenMap = new HashMap<>();
        final int SIZE = r.nextInt(4);
        for (int i = 0; i < SIZE; ++i) {
            DatabaseReference stolenObjectRef = generateRandomStolenObject (childRef.getKey());
            stolenMap.put(stolenObjectRef.getKey(), stolenObjectRef.getKey());
        }

        Report report = new Report();

        report.setDescription("I was in the street and I don't know what more I can say, " +
                "just that it was a very tragic moment in my life and I'm very sorry for this.");
        report.setId(childRef.getKey());
        report.setCrime_id(crimeRef.getKey());
        report.setDate(0);
        report.setStolen_objects(stolenMap);

        childRef.setValue(report);

        return childRef;
    }

    public static DatabaseReference generateRandomUser () {
        DatabaseReference ref = database.getReference (FirebaseReferences.USERS_REFERENCE);
        DatabaseReference childRef = ref.push ();
        DatabaseReference profileRef = generateRandomProfile(childRef.getKey());

        User user = new User();

        user.setEmail(childRef.getKey().substring(
                Math.min(10, childRef.getKey().length())
        ) + "@gmail.com");
        user.setProfile_id(profileRef.getKey());
        user.setId(childRef.getKey());
        user.setPhone_number("+57012345678" + r.nextInt(10));
        user.setIs_active_account(true);

        childRef.setValue (user);

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
            User.FriendRequestOut requestOut = new User.FriendRequestOut();
            User.FriendRequestIn requestIn = new User.FriendRequestIn();

            requestOut.setUser_id(userId);
            requestOut.setDate(0);

            friendsOut.child(userId).setValue(requestOut);

            requestIn.setUser_id(userId);
            requestIn.setDate(0);

            DatabaseReference friend = ref.child(userId).child(FirebaseReferences.User.FRIEND_REQUESTS_IN_REFERENCE).child(
                    childRef.getKey()
            );
            friend.setValue(requestIn);
        }

        friendsOutNum *= 2;
        for (int i = 0; i < friendsOutNum; ++i) {
            String userId = userIds.get(r.nextInt(userIds.size()));
            if (!friendsOutHash.contains(userId)) {
                User.Friend friend = new User.Friend();

                friend.setLocationShared(false);
                friend.setUser_id(userId);
                friend.setDate(0);

                childRef.child(FirebaseReferences.User.FRIENDS_REFERENCE).child(userId).setValue(friend);
            }
        }

        int reports = r.nextInt(11);

        for (int i = 0; i < reports; ++i) {
            DatabaseReference reportRef = generateRandomReport();
            childRef.child(FirebaseReferences.User.REPORTS_REFERENCE).child(reportRef.getKey()).setValue(reportRef.getKey());
        }

        userIds.add(childRef.getKey());

        return childRef;
    }

    public static void deleteValues () {
        database.getReference(FirebaseReferences.USERS_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.LOCATIONS_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.PROFILES_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.CRIMES_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.REPORTS_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.STOLEN_OBJECTS_REFERENCE).removeValue();

    }

    public static void generateValues (final int VAL) {
        DatabaseReference ref = database.getReference(FirebaseReferences.USERS_REFERENCE);
        for (int i = 0; i < VAL; ++i) {
           generateRandomUser();
        }
    }
}
