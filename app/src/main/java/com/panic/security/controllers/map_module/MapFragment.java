package com.panic.security.controllers.map_module;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.panic.security.R;

import com.panic.security.UserLocationUtils;
import com.panic.security.firebase_utils.DataCallback;
import com.panic.security.models.map_module.MapDrawer;

public class MapFragment extends Fragment {

    private MapView mMapView;
    private GoogleMap googleMap;
    private CameraPosition curCameraPosition;
    private MapDrawer mapDrawer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);

//        getUserLocation();

        getGoogleMap(view, savedInstanceState);

        return view;
    }

    private void getGoogleMap (View view, Bundle savedInstanceState) {

        mMapView = (MapView) view.findViewById (R.id.mapView);
        mMapView.onCreate (savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        mMapView.getMapAsync (new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled (true);

                googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        curCameraPosition = googleMap.getCameraPosition();
                    }
                });

                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker Title").snippet("Marker Description"));
                    }
                });

                mapDrawer = new MapDrawer(MapFragment.this);
                UserLocationUtils.getUserLastKnownLocation(getActivity(), new DataCallback<Location>() {
                    @Override
                    public void onDataReceive(Location location) {
                        mapDrawer.setMapStyle (location);
                        if (curCameraPosition == null) {
                            curCameraPosition = new CameraPosition.Builder().target(
                                    new LatLng(location.getLatitude(), location.getLongitude())
                            ).zoom(12).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(curCameraPosition));
                        } else {
                            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(curCameraPosition));
                        }
                    }
                });
            }

        });
    }

    public GoogleMap getGoogleMap () {
        return googleMap;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
