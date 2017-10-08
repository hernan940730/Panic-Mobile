package com.panic.security.models.map_module;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.panic.security.R;
import com.panic.security.utils.DataLoader;
import com.panic.security.utils.DataLoaderListener;
import com.panic.security.utils.ImageConverter;
import com.panic.security.utils.StorageManager;
import com.panic.security.utils.UserLocationUtils;
import com.panic.security.utils.DataCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by david on 9/18/17.
 */

public class MapDrawer implements DataLoaderListener {

    private final String TAG = "MapDrawer";

    private GoogleMap mMap;

    private Activity mainActivity;

    private JSONObject timeZoneJSON;

    private boolean isNormalMapStyle = true;


    private List<WeightedLatLng> weightedLatLngList;

    /**
     * For Heat Map Functionality
     */
    private HeatmapTileProvider heatmapTileProvider;

    private TileOverlay overlayOptions;

    private List<Marker> friendsMarkers;

    public MapDrawer (Activity activity, GoogleMap mMap) {
        this.mainActivity = activity;
        this.mMap = mMap;
        this.weightedLatLngList = new ArrayList<>();
        this.friendsMarkers = new ArrayList<>();
        this.weightedLatLngList = DataLoader.getInstance().getCrimeLocationList();

        DataLoader.getInstance().addOnCrimeChangedListener(this);
        UserLocationUtils.getInstance().addReceiveLocationListener(new DataCallback<Map<String, LatLng>>() {
            @Override
            public void onDataReceive(Map<String, LatLng> data) {
                drawFriendsOnMap(data);
            }
        });
    }

    private void drawFriendsOnMap (Map<String, LatLng> friends) {
        for (Marker friendMarker : friendsMarkers) {
            friendMarker.remove ();
        }

        friendsMarkers.clear();

        for (Map.Entry<String, LatLng> entry : friends.entrySet()) {
            String friendId = entry.getKey();
            Bitmap imageBitmap = StorageManager.loadProfileImage(friendId, mainActivity);
            BitmapDescriptor bitmapDescriptor = null;
            if (imageBitmap == null) {
                imageBitmap = BitmapFactory
                        .decodeResource(mainActivity.getResources(), R.mipmap.ic_account);
            }
            bitmapDescriptor = BitmapDescriptorFactory
                    .fromBitmap(
                            ImageConverter
                            .getMarkerBitmap(
                                    Bitmap.createScaledBitmap(
                                            imageBitmap, 150, 150, false
                                    )
                            )
                    );
            if (bitmapDescriptor == null) {
                StorageManager.saveProfileImage(friendId, mainActivity);
            }
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(entry.getValue())
                    .icon(bitmapDescriptor);
            friendsMarkers.add (mMap.addMarker(markerOptions));
        }
    }

    public void setMapStyle ( ) {
        UserLocationUtils.getInstance().getUserTimeZoneJSON (mainActivity, new DataCallback<JSONObject>() {
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

        if (heatmapTileProvider == null) {
            initHeatMap();
        }
        updateZones();
    }

    public void updateZones() {
        if (overlayOptions != null) {
            overlayOptions.remove();
        }
        initHeatMap();
    }

    private void initHeatMap() {
        if (weightedLatLngList == null || weightedLatLngList.size() == 0) {
            return;
        }
        heatmapTileProvider = new HeatmapTileProvider.Builder()
                .weightedData(weightedLatLngList)
                .gradient(
                        new Gradient(
                                new int[]{Color.TRANSPARENT, Color.YELLOW, Color.RED},
                                new float[]{0, 0.01F, 0.6F}
                        )
                )
                .opacity(0.4)
                .build();
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

    @Override
    public void onLoadCompleted() {
        updateZones();
    }
}
