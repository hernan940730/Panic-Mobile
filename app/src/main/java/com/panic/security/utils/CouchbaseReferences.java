package com.panic.security.utils;

/**
 * Created by david on 9/27/17.
 */

public class CouchbaseReferences {
    public static final String APP_NAME_REFERENCE = "app";

    public static final String USER_REFERENCE = "user";
    public static final String PROFILE_REFERENCE = "profile";
    public static final String CRIME_LOCATION_LIST_REFERENCE = "crime_location_list";

    public static class CrimeLocationList {
        public static final String REPORT_ID_REFERENCE = "report_id";
        public static final String LOCATION_ID_REFERENCE = "location_id";
        public static final String TYPE_REFERENCE = "type";
        public static final String DATE_REFERENCE = "date";
        public static final String CRIME_ID_REFERENCE = "crime_id";
        public static final String LATITUDE_REFERENCE = "latitude";
        public static final String LONGITUDE_REFERENCE = "longitude";
    }

}
