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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by david on 9/21/17.
 */

public class UserLocationUtils {

    private JSONObject userTimeZone;
    private final String GOOGLE_TIMEZONE_KEY = "key=AIzaSyDcuzG1jmEhUF3CQsw4nb4BTfarXjfSKUw";
    private final String GOOGLE_TIME_ZONE_URL = "https://maps.googleapis.com/maps/api/timezone/";
    private final String PANIC_LOCATION_URL = "http://192.168.0.22:5000/location/";
    private final String JSON_VALUE = "json?";
    private final String SET_VALUE = "set?";
    private final String GET_VALUE = "get?";
    private final String LOCATION_VALUE = "location=";
    private final String TIME_STAMP_VALUE = "timestamp=";
    private final String TOKEN_VALUE = "token=";

    private Handler locationHandler;
    private Runnable sendRunnable;
    private Runnable receiveRunnable;

    private static UserLocationUtils userLocationUtils;

    private UserLocationUtils() {
        locationHandler = new Handler();
    }

    public static synchronized UserLocationUtils getInstance () {
        if (userLocationUtils == null) {
            userLocationUtils = new UserLocationUtils();
        }
        return userLocationUtils;
    }

    public void getUserLastKnownLocation (Activity activity, final DataCallback<Location> callback) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient (activity);

        if (ActivityCompat.checkSelfPermission (activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener (
            activity,
            new OnSuccessListener<Location>() {
                @Override
                public void onSuccess (Location location) {
                    callback.onDataReceive (location);
                }
            }
        );
    }

    public synchronized void getUserTimeZoneJSON(Activity activity, final DataCallback<JSONObject> callback) {
        if (userTimeZone == null) {
            getUserLastKnownLocation(activity, new DataCallback<Location>() {
                @Override
                public void onDataReceive(Location location) {
                    if (location == null) {
                        return;
                    }
                    String requestURL =
                            GOOGLE_TIME_ZONE_URL +
                            JSON_VALUE +
                            LOCATION_VALUE +
                            location.getLatitude() + "," +
                            location.getLongitude() + "&" +
                            TIME_STAMP_VALUE + "0" + "&" +
                            GOOGLE_TIMEZONE_KEY;

                    new DownloadJSONTask(new DataCallback<JSONObject>() {
                            @Override
                            public void onDataReceive(JSONObject data) {
                                userTimeZone = data;
                                callback.onDataReceive(data);
                            }
                        }
                    ).execute(requestURL);
                }
            });
        }
        else {
            callback.onDataReceive(userTimeZone);
        }
    }

    public void addReceiveLocationListener(final DataCallback<Map<String, LatLng>> listener) {
        receiveRunnable = new ReceiveLocationRunnable (listener);
        addReceiveLocationListener();
    }

    private void addReceiveLocationListener () {
        locationHandler.postDelayed (receiveRunnable, 5000);
    }

    public void revokeReceiveLocationListener () {
        locationHandler.removeCallbacks (receiveRunnable);
    }

    public void addSendLocationListener (final Activity activity) {
        sendRunnable = new SendLocationRunnable (activity);
        addSendLocationListener ();
    }

    private void addSendLocationListener () {
        locationHandler.postDelayed (sendRunnable, 5000);
    }

    public void revokeSendLocationListener () {
        locationHandler.removeCallbacks (sendRunnable);
    }

    private class DownloadJSONTask extends AsyncTask<String, Void, JSONObject> {

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

        public JSONObject getResponseJSON(String stringUrl) throws IOException {
            URL url = new URL (stringUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            return getResponseJSON (httpConn);
        }

        private JSONObject getResponseJSON(HttpURLConnection httpConn) throws IOException {
            StringBuilder response  = new StringBuilder();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(httpConn.getInputStream()), 8192);
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

    private class ReceiveLocationRunnable implements Runnable {

        DataCallback<Map<String, LatLng>> listener;

        public ReceiveLocationRunnable (DataCallback<Map<String, LatLng>> listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                return;
            }
            final String userUid = user.getUid();

            String url = PANIC_LOCATION_URL +
                    GET_VALUE +
                    TOKEN_VALUE +
                    userUid;

            new DownloadJSONTask(
                new DataCallback<JSONObject>() {
                    @Override
                    public void onDataReceive(JSONObject data) {
                        if (data != null) {
                            Map<String, LatLng> friendsLocation = new HashMap<>();
                            Iterator<String> it = data.keys();
                            while (it.hasNext()) {
                                String friendUid = it.next();
                                try {
                                    JSONObject friendLocation = data.getJSONObject(friendUid);
                                    double lat = friendLocation.getDouble("latitude");
                                    double lng = friendLocation.getDouble("longitude");
                                    friendsLocation.put(friendUid, new LatLng(lat, lng));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            listener.onDataReceive (friendsLocation);
                        }
                        addReceiveLocationListener();
                    }
                }
            ).execute (url);
        }
    }

    private class SendLocationRunnable implements Runnable {

        private Activity activity;

        private SendLocationRunnable(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            final String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            getUserLastKnownLocation(activity, new DataCallback<Location>() {
                @Override
                public void onDataReceive(Location location) {
                    if (location == null) {
                        return;
                    }

                    String url = PANIC_LOCATION_URL +
                            SET_VALUE +
                            TOKEN_VALUE +
                            userUid +
                            "&" +
                            LOCATION_VALUE +
                            location.getLatitude() + "," + location.getLongitude();

                    new DownloadJSONTask(
                        new DataCallback<JSONObject>() {
                            @Override
                            public void onDataReceive(JSONObject data) {
                                addSendLocationListener();
                            }
                        }
                    ).execute (url);
                }
            });
        }
    }

}
