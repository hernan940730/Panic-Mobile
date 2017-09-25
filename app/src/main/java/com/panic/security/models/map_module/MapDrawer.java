package com.panic.security.models.map_module;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.entities.Location;
import com.panic.security.firebase_utils.FirebaseDAO;
import com.panic.security.location_utils.UserLocationUtils;
import com.panic.security.firebase_utils.DataCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by david on 9/18/17.
 */

public class MapDrawer {

    private final String TAG = "MapDrawer";

    private GoogleMap mMap;

    private Activity mainActivity;

    private JSONObject timeZoneJSON;

    private boolean isNormalMapStyle = true;

    /**
     * For Heat Map Functionality
     */
    private List<WeightedLatLng> reportsLatLng;
    private HeatmapTileProvider heatmapTileProvider;
    private TileOverlay overlayOptions;
    private double EPS = 1e-100;

    public MapDrawer (Activity activity, GoogleMap mMap) {
        this.mainActivity = activity;
        this.mMap = mMap;
        this.reportsLatLng = new ArrayList<>();
        this.reportsLatLng.add (new WeightedLatLng (new LatLng (-76.337187, 22.330905), EPS));
    }

    public void setMapStyle ( ) {
        UserLocationUtils.getUserTimeZoneJSON (mainActivity, new DataCallback<JSONObject>() {
            @Override
            public void onDataReceive(JSONObject data) {
                timeZoneJSON = data;
                drawMapStyle();
            }
        });
    }

    private boolean isSunlight (TimeZone timeZone) {
        boolean ret = false;
        Calendar calendar = Calendar.getInstance(timeZone);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 6 && hour < 18) {
            ret = true;
        }

        return ret;
    }

    private void drawMapStyle () {
        if (timeZoneJSON == null) {
            return;
        }
        try {
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneJSON.getString("timeZoneId"));

            if (isSunlight (timeZone)) {
                if (!isNormalMapStyle) {
                    isNormalMapStyle = true;
                    boolean success = mMap.setMapStyle(null);
                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                }
            }
            else {
                if (isNormalMapStyle) {
                    isNormalMapStyle = false;
                    boolean success = mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    mainActivity, R.raw.dark_styled_map));

                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void drawZones () {
        addHeatMap();
        FirebaseDAO.getInstance().getReportLocationsList(new DataCallback<List<Location>>() {
            @Override
            public void onDataReceive(List<Location> data) {
                List<WeightedLatLng> latLngList = new ArrayList<>();
                for (Location loc : data) {
                    latLngList.add(new WeightedLatLng(new LatLng(loc.getLatitude(), loc.getLongitude())));
                }
                reportsLatLng = latLngList;
                heatmapTileProvider.setWeightedData(reportsLatLng);
            }
        });

    }

    private void addHeatMap () {
        heatmapTileProvider = new HeatmapTileProvider.Builder().
                weightedData(reportsLatLng).
                opacity(0.3).
                build();
        overlayOptions = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
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
