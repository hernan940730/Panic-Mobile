package com.panic.security;

/**
 * Created by david on 9/3/17.
 */

public abstract class FirebaseReferences {
    /**
     * Database reference
     */
    public static final String DATABASE_REFERENCE = "panic-security";

    /**
     * Collections references
     */
    public static final String CRIMES_REFERENCE = "crimes";
    public static final String LOCATIONS_REFERENCE = "locations";
    public static final String PROFILES_REFERENCE = "profiles";
    public static final String REPORTS_REFERENCE = "reports";
    public static final String STOLEN_OBJECTS_REFERENCE = "stolen_objects";
    public static final String USERS_REFERENCE = "users";

    /**
     * Welcome message reference
     */
    public static final String MESSAGE_REFERENCE = "home_message";

    public static abstract class Crime {
        public static final String TYPE_REFERENCE = "type";
        public static final String DATE_REFERENCE = "date";
    }

    public static abstract class Report {
        public static final String USER_ID_REFERENCE = "user_id";
        public static final String CRIME_ID_REFERENCE = "crime_id";
        public static final String LOCATION_ID_REFERENCE = "location_id";
        public static final String DATE_REFERENCE = "date";
        public static final String DESCRIPTION_REFERENCE = "description";
    }

    public static abstract class Location {
        public static final String LATITUDE_REFERENCE = "latitude";
        public static final String LONGITUDE_REFERENCE = "longitude";
    }

    public static abstract class StolenObject {
        public static final String REPORT_ID_REFERENCE = "report_id";
        public static final String NAME_REFERENCE = "name";
        public static final String DESCRIPTION_REFERENCE = "description";
    }

    public static abstract class User {
        public static final String PROFILE_ID_REFERENCE = "profile_id";
        public static final String EMAIL_REFERENCE = "email";
        public static final String PASSWORD_REFERENCE = "password";
        public static final String PROFILE_PICTURE_REFERENCE = "profile_picture";
        public static final String PHONE_NUMBER_REFERENCE = "phone_number";
        public static final String IS_ACTIVE_REFERENCE = "is_active_account";
        public static final String FRIENDS_REFERENCE = "friends";
        public static final String FRIEND_REQUESTS_REFERENCE = "friend_requests";

        public static abstract class Friend {
            public static final String USER_ID = "user_id";
            public static final String DATE = "date";
            public static final String IS_LOCATION_SHARED = "is_location_shared";
            public static final String IS_PENDING = "is_pending";

        }

        public static abstract class FriendRequest {
            public static final String USER_ID = "user_id";
            public static final String DATE = "date";
        }
    }

    public static abstract class Profile {
        public static final String NAME_REFERENCE = "name";
        public static final String LASTNAME_REFERENCE = "last_name";
        public static final String BIRTHDAY_REFERENCE = "birthday";
        public static final String GENDER_REFERENCE = "gender";
    }
}
