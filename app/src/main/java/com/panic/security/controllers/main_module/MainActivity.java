package com.panic.security.controllers.main_module;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.panic.security.R;
import com.panic.security.controllers.login_sign_up_module.LoginActivity;

import com.panic.security.controllers.user_profile_module.UserProfileFragment;
import com.panic.security.entities.Profile;
import com.panic.security.entities.User;
import com.panic.security.firebase_utils.DataCallback;
import com.panic.security.firebase_utils.FirebaseDAO;
import com.panic.security.location_utils.UserLocationUtils;
import com.panic.security.models.map_module.MapDrawer;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback{

    private final String TAG = "MainActivity";

    // Authentication with FireBase
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    private CameraPosition curCameraPosition;
    private MapDrawer mapDrawer;

    public final int locationRequestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocationPermission();

        mAuth = FirebaseAuth.getInstance();

        updateUI();
    }

    private void showLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity( intent );
        finish();
    }

    private void updateUI() {
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        configMenu();
        //addSearchBar();
    }

    /* Menu navigator*/
    public void configMenu(){

        addHeaderMenu();

        //To navigate with the menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //For can access to text view inside nav_header_menu
        final View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {

            FirebaseDAO.getInstance().getUserByID(mAuth.getCurrentUser().getUid(), new DataCallback<User>() {
                @Override
                public void onDataReceive(User user) {

                TextView textUserEmail = (TextView) headerView.findViewById(R.id.text_email_user);
                textUserEmail.setText(user.getEmail());

                FirebaseDAO.getInstance().getProfileByID(user.getProfile_id(), new DataCallback<Profile>() {
                    @Override
                    public void onDataReceive(Profile profile) {

                    TextView textNameUser = (TextView) headerView.findViewById(R.id.text_name_user);
                    textNameUser.setText(profile.getName() + " " + profile.getLast_name());

                    }
                });

                }
            });

        }
    }

    public void addHeaderMenu(){
        // take toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add burger icon
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.item_home) {
            fragmentManager.beginTransaction().replace (R.id.content_main, new Fragment()).commit();
        } else if (id == R.id.item_user_profile) {
            fragmentManager.beginTransaction().replace (R.id.content_main, new UserProfileFragment()).commit();
        } else if (id == R.id.item_friends) {

        } else if (id == R.id.item_my_reports) {

        } else if (id == R.id.item_notifications) {

        } else if (id == R.id.item_about) {

        } else if ( id == R.id.item_sign_out ){
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        mAuth.signOut();
        showLogin();
    }

    private void getLocationPermission () {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions (this, permissions, locationRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case locationRequestCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    finishAffinity();
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        // For showing a move to my location button
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled (true);

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                curCameraPosition = mMap.getCameraPosition();
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker Title").snippet("Marker Description"));
                CameraUpdate crimeLocation = CameraUpdateFactory.newLatLng( latLng );
                mMap.animateCamera( crimeLocation );
            }
        });

        mapDrawer = new MapDrawer(this, mMap);
        mapDrawer.setMapStyle ();
        mapDrawer.drawZones ();

        if (curCameraPosition != null) {
            moveCamera(curCameraPosition, false);
        }
        else {
            UserLocationUtils.getUserLastKnownLocation (this, new DataCallback<Location>() {
                @Override
                public void onDataReceive (Location location) {
                    CameraPosition cameraPosition = null;
                    if (location != null) {
                        cameraPosition = new CameraPosition.Builder ().
                                target (new LatLng (location.getLatitude (), location.getLongitude ())).
                                zoom (12).
                                build ();
                    }
                    moveCamera (cameraPosition, true);
                }
            });
        }

    }
    private void moveCamera(CameraPosition cameraPosition, boolean animateCamera) {
        if (cameraPosition == null) {
            return;
        }
        if (animateCamera) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
}
