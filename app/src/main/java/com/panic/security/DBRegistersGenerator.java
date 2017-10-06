package com.panic.security;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.panic.security.entities.Crime;
import com.panic.security.entities.Friend;
import com.panic.security.entities.FriendRequest;
import com.panic.security.entities.Location;
import com.panic.security.entities.Profile;
import com.panic.security.entities.Report;
import com.panic.security.entities.StolenObject;
import com.panic.security.entities.User;
import com.panic.security.utils.FirebaseReferences;

import java.util.ArrayList;
import java.util.Calendar;
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

    private static String countries[] = {"colombia", "united_states", "france", "italy", "japan", "mexico", "england" };

    private static double latFrom = 4.5;
    private static double latTo = 4.75;
    private static double longFrom = -74.2;
    private static double longTo = -74;

    private static final int CURRENT_YEAR = 2017;
    private static final int CURRENT_MONTH = 8;
    private static final int[] DAYS_IN_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private static ArrayList<String> userIds = new ArrayList<>();

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static int getRandomYear (int from, int to) {
        if (to < from) {
            return 0;
        }
        return from + r.nextInt(to - from + 1);
    }

    private static int getRandomMonth (int year) {
        if (year == CURRENT_YEAR) {
            return r.nextInt (CURRENT_MONTH);
        }
        return r.nextInt (12);
    }

    private static int getRandomDate (int month) {
        return r.nextInt(DAYS_IN_MONTH[month] + 1);
    }

    private static long getRandomDateInMillis () {
        Calendar c = Calendar.getInstance();
        int year = getRandomYear(2015, CURRENT_YEAR);
        int month = getRandomMonth(year);
        int date = getRandomDate(month);
        int hour = r.nextInt(24);
        int minute = r.nextInt(60);
        c.set(year, month, date, hour, minute);
        return c.getTimeInMillis();
    }

    private static long getRandomBirthdayInMillis () {
        Calendar c = Calendar.getInstance();
        int year = getRandomYear(1960, 2000);
        int month = getRandomMonth(year);
        int date = getRandomDate(month);
        int hour = r.nextInt(24);
        int minute = r.nextInt(60);
        c.set(year, month, date, hour, minute);
        return c.getTimeInMillis();
    }

    private static long getCurrentDateInMillis () {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }

    public static StolenObject generateRandomStolenObject (String reportId) {
        DatabaseReference ref = database.getReference(FirebaseReferences.STOLEN_OBJECTS_REFERENCE).push();

        StolenObject stolenObject = new StolenObject(
                ref.getKey(),
                reportId,
                stolenObjects[r.nextInt(stolenObjects.length)],
                "It was white and beauty, it had many gorgeous things."
        );

        ref.setValue (stolenObject);

        return stolenObject;
    }

    public static Crime generateRandomCrime (String reportId) {
        DatabaseReference ref = database.getReference(FirebaseReferences.CRIMES_REFERENCE).push();

        Location location = generateRandomLocation(ref.getKey());

        Crime crime = new Crime(
                ref.getKey(),
                reportId,
                location.getId(),
                crimeList[r.nextInt(crimeList.length)],
                getRandomDateInMillis()
        );

        ref.setValue (crime);

        return crime;
    }

    public static Location generateRandomLocation (String crimeId) {
        DatabaseReference ref = database.getReference(FirebaseReferences.LOCATIONS_REFERENCE).push();

        Location location = new Location(
                ref.getKey(),
                crimeId,
                r.nextDouble() * (latTo - latFrom) + latFrom,
                r.nextDouble() * (longTo - longFrom) + longFrom
        );

        ref.setValue(location);

        return location;
    }

    public static Profile generateRandomProfile (String userId) {
        DatabaseReference ref = database.getReference(FirebaseReferences.PROFILES_REFERENCE).push();

        Profile profile = new Profile(
                ref.getKey(),
                userId,
                getRandomBirthdayInMillis(),
                gender[r.nextInt(gender.length)],
                lastnamesList[r.nextInt(lastnamesList.length)],
                namesList[r.nextInt(namesList.length)],
                countries[r.nextInt(countries.length)]
        );

        ref.setValue (profile);

        return profile;
    }

    public static void joinReportWithStolenObject(String reportId, String stolenObjectId) {
        DatabaseReference ref = database.getReference(FirebaseReferences.REPORT_STOLEN_OBJECTS_REFERENCE).child (reportId);
        ref.child(stolenObjectId).setValue(stolenObjectId);
    }

    public static Report generateRandomReport (String userId) {
        DatabaseReference ref = database.getReference(FirebaseReferences.REPORTS_REFERENCE).push();
        Crime crimeRef = generateRandomCrime(ref.getKey());

        Report report = new Report(
                ref.getKey(),
                userId,
                crimeRef.getId(),
                getCurrentDateInMillis(),
                "I was in the street and I don't know what more I can say, " +
                        "just that it was a very tragic moment in my life and I'm very sorry for this."
        );

        final int SIZE = r.nextInt(4);
        for (int i = 0; i < SIZE; ++i) {
            StolenObject stolenObjectRef = generateRandomStolenObject (ref.getKey());
            joinReportWithStolenObject(report.getId(), stolenObjectRef.getId());
        }

        ref.setValue(report);

        return report;
    }

    private static void joinUserWithFriends (String userId) {
        HashSet<String> friendsOutHash = new HashSet<>();

        DatabaseReference friendsOutRef = database.getReference (FirebaseReferences.USER_FRIEND_REQUESTS_OUT_REFERENCE);
        DatabaseReference friendsInRef = database.getReference (FirebaseReferences.USER_FRIEND_REQUESTS_IN_REFERENCE);
        DatabaseReference friendsRef = database.getReference (FirebaseReferences.USER_FRIENDS_REFERENCE);

        int friendsOutNum = 0;
        if (!userIds.isEmpty()) {
            friendsOutNum = r.nextInt(userIds.size());
        }

        for (int i = 0; i < friendsOutNum; ++i) {
            String friendOutID = userIds.get (r.nextInt (userIds.size()));
            friendsOutHash.add (friendOutID);
        }

        for (String friendId : friendsOutHash) {
            FriendRequest requestOut = new FriendRequest(
                    friendId,
                    getCurrentDateInMillis()
            );
            FriendRequest requestIn = new FriendRequest(
                    friendId,
                    getCurrentDateInMillis()
            );

            friendsOutRef.child(userId).child(friendId).setValue(requestOut);
            friendsInRef.child(friendId).child(userId).setValue(requestIn);
        }

        friendsOutNum *= 2;
        for (int i = 0; i < friendsOutNum; ++i) {
            String friendId = userIds.get(r.nextInt(userIds.size()));
            if (!friendsOutHash.contains(friendId)) {

                Friend friend = new Friend(
                        friendId,
                        getCurrentDateInMillis(),
                        false
                );

                Friend friend2 = new Friend(
                        userId,
                        getCurrentDateInMillis(),
                        false
                );

                friendsRef.child(userId).child(friendId).setValue(friend);
                friendsRef.child(friendId).child(userId).setValue(friend2);
            }
        }
    }

    private static void joinUserWithReport (String userId, String reportId) {
        DatabaseReference ref = database.getReference (FirebaseReferences.USER_REPORTS_REFERENCE);
        ref.child(userId).child(reportId).setValue(reportId);
    }

    public static User generateRandomUser () {
        DatabaseReference ref = database.getReference (FirebaseReferences.USERS_REFERENCE).push();
        Profile profileRef = generateRandomProfile(ref.getKey());

        User user = new User(
                ref.getKey(),
                profileRef.getName() + "_" + profileRef.getLast_name() +
                        ref.getKey().substring(ref.getKey().length() - 4) +
                        "@gmail.com",
                true,
                "+57012345678" + r.nextInt(10),
                profileRef.getId()
        );

        ref.setValue (user);

        joinUserWithFriends(user.getId());

        int reports = r.nextInt(11);

        for (int i = 0; i < reports; ++i) {
            Report report = generateRandomReport(user.getId());
            joinUserWithReport(user.getId(), report.getId());
        }

        userIds.add(ref.getKey());

        return user;
    }

    public static void deleteValues () {
        database.getReference(FirebaseReferences.USERS_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.LOCATIONS_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.PROFILES_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.CRIMES_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.REPORTS_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.STOLEN_OBJECTS_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.USER_FRIENDS_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.USER_FRIEND_REQUESTS_IN_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.USER_FRIEND_REQUESTS_OUT_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.USER_REPORTS_REFERENCE).removeValue();
        database.getReference(FirebaseReferences.REPORT_STOLEN_OBJECTS_REFERENCE).removeValue();
    }

    public static void generateValues (final int VAL) {
        DatabaseReference ref = database.getReference(FirebaseReferences.USERS_REFERENCE);
        for (int i = 0; i < VAL; ++i) {
           generateRandomUser();
        }
    }
}
