package com.panic.security.models.map_module;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.panic.security.R;
import com.panic.security.UserLocationUtils;
import com.panic.security.controllers.map_module.MapFragment;
import com.panic.security.firebase_utils.DataCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by david on 9/18/17.
 */

public class MapDrawer {

    private final String TAG = "MapDrawer";

    private final double MAX_DIST = 30;

    private GoogleMap map;

    private MapFragment mapFragment;

    private JSONObject timeZoneJSON;

    private boolean isNormalMapStyle = true;

    public MapDrawer (MapFragment mapFragment) {
        this.mapFragment = mapFragment;
        this.map = mapFragment.getGoogleMap();
    }

    public void setMapStyle (Location location) {
        UserLocationUtils.getUserTimeZoneJSON(mapFragment.getActivity(), new DataCallback<JSONObject>() {
            @Override
            public void onDataReceive(JSONObject data) {
                timeZoneJSON = data;
                drawMapStyle();
            }
        });
    }

    private boolean isSunlight(TimeZone timeZone) {
        boolean ret = false;
        Calendar calendar = Calendar.getInstance(timeZone);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 6 && hour < 18) {
            ret = true;
        }

        return ret;
    }

    private void drawMapStyle () {
        if (timeZoneJSON != null) {
            try {
                TimeZone timeZone = TimeZone.getTimeZone(timeZoneJSON.getString("timeZoneId"));

                if (isSunlight (timeZone)) {
                    if (!isNormalMapStyle) {
                        isNormalMapStyle = true;
                        boolean success = map.setMapStyle(null);
                        if (!success) {
                            Log.e(TAG, "Style parsing failed.");
                        }
                    }
                }
                else {
                    if (isNormalMapStyle) {
                        isNormalMapStyle = false;
                        boolean success = map.setMapStyle(
                                MapStyleOptions.loadRawResourceStyle(
                                        mapFragment.getActivity(), R.raw.dark_styled_map));

                        if (!success) {
                            Log.e(TAG, "Style parsing failed.");
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawZones () {

    }

    private double getDistanceFromLatLngInKm (LatLng latLng1, LatLng latLng2) {
        double  lat1 = latLng1.latitude,
                lon1 = latLng1.longitude,
                lat2 = latLng2.latitude,
                lon2 = latLng2.longitude;

        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad (lat2 - lat1);  // deg2rad below
        double dLon = deg2rad (lon2 - lon1);
        double a = Math.sin (dLat / 2.0) * Math.sin (dLat / 2.0) +
                Math.cos (deg2rad (lat1)) * Math.cos (deg2rad (lat2)) *
                        Math.sin (dLon / 2.0) * Math.sin (dLon / 2.0);
        double c = 2.0 * Math.atan2 (Math.sqrt (a), Math.sqrt (1.0 - a));
        double d = R * c; // Distance in km
        return d;
    }

    private double deg2rad (double deg) {
        return deg * (Math.PI / 180.0);
    }

}
