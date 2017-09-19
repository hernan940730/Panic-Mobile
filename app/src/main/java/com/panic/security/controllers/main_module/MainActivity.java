package com.panic.security.controllers.main_module;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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

import com.google.firebase.auth.FirebaseAuth;
import com.panic.security.R;
import com.panic.security.controllers.map_module.MapFragment;
import com.panic.security.controllers.login_sign_up_module.LoginActivity;

import com.panic.security.controllers.user_profile_module.UserProfileFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private final String TAG = "MainActivity";

    // Authentication with FireBase
    private FirebaseAuth mAuth;
    private MapFragment mapFragment;

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
        configMenu();
    }

    /* Menu navigator*/
    public void configMenu(){

        // take toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add burger icon
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //To navigate with the menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //For can access to text view inside nav_header_menu
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            TextView textUserEmail = (TextView) headerView.findViewById(R.id.text_email_user);
            if(textUserEmail != null){
                textUserEmail.setText(mAuth.getCurrentUser().getEmail());
            }
        }
        mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mapFragment).commit();
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
            fragmentManager.beginTransaction().replace(R.id.content_main, mapFragment).commit();
        } else if (id == R.id.item_user_profile) {
            fragmentManager.beginTransaction().replace(R.id.content_main, new UserProfileFragment()).commit();
        } else if (id == R.id.item_friends) {

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
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, locationRequestCode);
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

}
