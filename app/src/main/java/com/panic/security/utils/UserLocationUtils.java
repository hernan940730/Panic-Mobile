package com.panic.security.utils;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.panic.security.utils.DataCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by david on 9/21/17.
 */

public class UserLocationUtils {

    private static JSONObject userTimeZone;
    private static final String GOOGLE_TIMEZONE_KEY = "key=AIzaSyDcuzG1jmEhUF3CQsw4nb4BTfarXjfSKUw";
    private static final String GOOGLE_TIME_ZONE_URL = "https://maps.googleapis.com/maps/api/timezone/";
    private static final String JSON_VALUE = "json?";
    private static final String LOCATION_VALUE = "location=";
    private static final String TIME_STAMP_VALUE = "timestamp=";


    private UserLocationUtils() {

    }

    public static void addOnLocationProviderEnableListener(final Activity activity, final DataCallback<Location> callback) {

        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged (Location location) {

            }

            public void onStatusChanged (String provider, int status, Bundle extras) {

            }

            public void onProviderEnabled (String provider) {
                getUserLastKnownLocation (activity, callback);
            }

            public void onProviderDisabled (String provider) {
            }
        };

// Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission (activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates (LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public static void getUserLastKnownLocation (Activity activity, final DataCallback<Location> callback) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient (activity);

        if (ActivityCompat.checkSelfPermission (activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener (activity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                callback.onDataReceive (location);
            }
        });

    }

    public static synchronized void getUserTimeZoneJSON(Activity activity, final DataCallback<JSONObject> callback) {
        if (userTimeZone == null) {
            getUserLastKnownLocation(activity, new DataCallback<Location>() {
                @Override
                public void onDataReceive(Location location) {
                    if (location == null) {
                        return;
                    }
                    String requestURL = GOOGLE_TIME_ZONE_URL + JSON_VALUE + LOCATION_VALUE +
                            location.getLatitude() + "," + location.getLongitude() + "&" + TIME_STAMP_VALUE + "0" + "&" + GOOGLE_TIMEZONE_KEY;

                    new DownloadJSONTask(new DataCallback<JSONObject>() {
                        @Override
                        public void onDataReceive(JSONObject data) {
                            userTimeZone = data;
                            callback.onDataReceive(data);
                        }
                    }).execute(requestURL);
                }
            });
        }
        else {
            callback.onDataReceive(userTimeZone);
        }

    }

    private static class DownloadJSONTask extends AsyncTask<String, Void, JSONObject> {

        private DataCallback<JSONObject> callback;

        public DownloadJSONTask (DataCallback<JSONObject> callback) {
            this.callback = callback;
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            try {
                return getResponseJSON (urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            callback.onDataReceive (jsonObject);
        }

        private JSONObject getResponseJSON(String stringUrl) throws IOException {
            StringBuilder response  = new StringBuilder();

            URL url = new URL (stringUrl);
            HttpsURLConnection httpsconn = (HttpsURLConnection) url.openConnection();
            if (httpsconn.getResponseCode() == HttpsURLConnection.HTTP_OK)
            {
                BufferedReader input = new BufferedReader(new InputStreamReader(httpsconn.getInputStream()), 8192);
                String strLine;
                while ((strLine = input.readLine()) != null)
                {
                    response.append(strLine);
                }
                input.close();
            }
            JSONObject json = null;

            try {
                json = new JSONObject(response.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }

    }

}
