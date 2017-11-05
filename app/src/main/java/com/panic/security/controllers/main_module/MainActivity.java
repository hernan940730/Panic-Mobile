package com.panic.security.controllers.main_module;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.panic.security.R;
import com.panic.security.controllers.friends_module.FriendsFragment;
import com.panic.security.controllers.login_sign_up_module.LoginActivity;

import com.panic.security.controllers.reports_module.ReportCreateFragment;
import com.panic.security.controllers.reports_module.ReportsFragment;
import com.panic.security.controllers.user_profile_module.UserProfileFragment;
import com.panic.security.entities.Crime;
import com.panic.security.entities.Friend;
import com.panic.security.entities.Profile;
import com.panic.security.entities.Report;
import com.panic.security.entities.User;
import com.panic.security.utils.CouchbaseDAO;
import com.panic.security.utils.DataCallback;
import com.panic.security.utils.DataLoader;
import com.panic.security.utils.FirebaseDAO;
import com.panic.security.utils.FirebaseReferences;
import com.panic.security.utils.ListAdapter;
import com.panic.security.utils.UserLocationUtils;
import com.panic.security.models.map_module.MapDrawer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, View.OnClickListener{

    private final String TAG = "MainActivity";
    private Set<String> friendsIDs;

    private static String CRIMES_LIST[] = {
            "assault_crime",
            "auto_theft_crime",
            "burglary_crime",
            "shop_lifting_crime",
            "suspicious_activity_crime",
            "homicide_crime",
            "vandalism_crime",
            "drugs_crime",
            "other_crime"
    };

    private static int NUM_LINES = 6;

    private boolean isMarker = false;
    private boolean reportMade = false;
    private String mText = "";

    // Authentication with FireBase
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    private CameraPosition curCameraPosition;
    private MapDrawer mapDrawer;
    private ImageButton crimes;
    private ImageButton closeButton;
    private Animation animFadeIn ;
    private LatLng location;
    private Marker marker;
    public final int locationRequestCode = 1;

    private boolean isSharing = false;

    // Search bar
    MaterialSearchView mSearchView;
    List<String> mListSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocationPermission();
        handlerPassDataBetweenFragments();

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
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        //addSearchBar();
        crimes = findViewById( R.id.other_button );

        setUpCrimesButtons();

        final Button shareLocationButton = findViewById(R.id.share_location_button);
        shareLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSharing) {
                    UserLocationUtils.getInstance().revokeSendLocationListener();
                    shareLocationButton.setText(R.string.share_location);
                    shareLocationButton.setBackgroundResource (R.color.success_color);
                    isSharing = !isSharing;
                } else {
                    FirebaseDAO.getInstance().getUserFriends(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            new DataCallback<Map<String, Friend>>() {
                                @Override
                                public void onDataReceive(Map<String, Friend> friends) {
                                    showDialog(MainActivity.this, friends, shareLocationButton);
                                }
                            }
                    );
                }
            }
        });

    }

    public boolean showDialog(Context context, Map<String, Friend> friends, final Button shareLocationButton){

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_list_friends);

        friendsIDs = new HashSet<>();

        final ListView listViewFriends = dialog.findViewById(R.id.dialog_list_view_friends);
        final ListAdapter adapter = new ListAdapter(MainActivity.this);

        if(friends != null){

            for (Map.Entry<String, Friend> friend : friends.entrySet()){
                FirebaseDAO.getInstance().getUserByID(friend.getKey(), new DataCallback<User>() {
                    @Override
                    public void onDataReceive(final User user) {

                        FirebaseDAO.getInstance().getProfileByID(user.getProfile_id(), new DataCallback<Profile>() {
                            @Override
                            public void onDataReceive(final Profile profile) {
                                adapter.addItem(true, user, (profile.getName() + " " + profile.getLast_name()), user.getEmail());
                                listViewFriends.setAdapter(adapter);
                            }
                        });

                    }
                });
            }

            // Event when one item is selected
            listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    String friendID = adapter.getUserByPosition(position).getId();
                    int viewId = view.getId();
                    switch (viewId) {
                        case R.id.share_check_box:
                            if(friendsIDs.contains(friendID)){
                                friendsIDs.remove(friendID);
                            }else{
                                friendsIDs.add(friendID);
                            }
                        break;
                    }
                }
            });
        }

        Button cancelShareButton = dialog.findViewById(R.id.cancel_share_button);
        cancelShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button shareButton = dialog.findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                friendsIDs.size();

                UserLocationUtils.getInstance().addSendLocationListener(MainActivity.this);
                shareLocationButton.setText(R.string.stop_share_location);
                shareLocationButton.setBackgroundResource (R.color.failure_color);
                isSharing = !isSharing;
                dialog.dismiss();
            }
        });

        dialog.show();

        return false;
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if( isMarker ) {
            marker.remove();
            isMarker = false;
            hideCrimesButtons();
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
            fragmentManager.beginTransaction().replace (R.id.content_main, new UserProfileFragment(), "UserProfileFragment").commit();
        } else if (id == R.id.item_friends) {
            fragmentManager.beginTransaction().replace (R.id.content_main, new FriendsFragment(), "FriendsFragment").commit();
        } else if (id == R.id.item_my_reports) {
            fragmentManager.beginTransaction().replace (R.id.content_main, new ReportsFragment(), "ReportsFragment").commit();
        } /*else if (id == R.id.item_notifications) {
            fragmentManager.beginTransaction().replace (R.id.content_main, new NotificationsFragment()).commit();
        } */else if (id == R.id.item_about) {

        } else if ( id == R.id.item_sign_out ){
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handlerPassDataBetweenFragments(){
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        if(type != null){
            FragmentManager fragmentManager = getSupportFragmentManager();

            if(type.equals("list_friends")){
                User userFound = (User)intent.getSerializableExtra("user_selected");
                Bundle bundle = new Bundle();
                bundle.putSerializable("userFound", userFound);
                UserProfileFragment userProfileFragment = new UserProfileFragment();
                userProfileFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.content_main, userProfileFragment).commit();
            }
        }
    }

    private void signOut() {
        CouchbaseDAO.getInstance().deleteData();
        UserLocationUtils.getInstance().revokeSendLocationListener();
        UserLocationUtils.getInstance().revokeReceiveLocationListener();
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

        mMap.getUiSettings().setMapToolbarEnabled( false );

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if( isMarker ){
                    marker.remove();
                    isMarker = false;
                }
                isMarker = true;
                location = latLng;
                marker = mMap.addMarker( new MarkerOptions().position(latLng) );
                CameraUpdate crimeLocation = CameraUpdateFactory.newLatLng( latLng );
                mMap.animateCamera( crimeLocation );
                showCrimesButtons();
            }
        });

        mapDrawer = new MapDrawer(this, mMap);
        mapDrawer.setMapStyle ();
        mapDrawer.drawZones ();

        if (curCameraPosition != null) {
            moveCamera(curCameraPosition, false);
        }
        else {
            UserLocationUtils.getInstance().getUserLastKnownLocation (this, new DataCallback<Location>() {
                @Override
                public void onDataReceive (Location location) {
                    CameraPosition cameraPosition = null;
                    if (location != null) {
                        cameraPosition = new CameraPosition.Builder ().
                                target (new LatLng (location.getLatitude (), location.getLongitude ())).
                                zoom (17).
                                build ();
                    }
                    moveCamera (cameraPosition, true);
                }
            });
        }

    }

    private void showCrimesButtons() {
            crimes.setVisibility( View.VISIBLE );
    }

    private void setUpCrimesButtons() {
        crimes.setVisibility( View.GONE );
        crimes.setOnClickListener( this );
    }

    private void hideCrimesButtons() {
        crimes.setVisibility( View.INVISIBLE );
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

    @Override
    public void onClick(View view) {
        // Pass marker longitud and latitude
        Bundle args = new Bundle();
        args.putDouble( "marker_lat", location.latitude );
        args.putDouble( "marker_lon", location.longitude );
        ReportCreateFragment reportCreateFragment = new ReportCreateFragment();
        reportCreateFragment.setArguments( args );
        // Show Reports Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        marker.remove();
        hideCrimesButtons();
        fragmentManager.beginTransaction().replace (R.id.content_main, reportCreateFragment ).addToBackStack( null ).commit();
    }

    public void addSearchBar(){

            mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
            mSearchView.setVoiceSearch(false);
            mSearchView.setCursorDrawable(R.drawable.custom_cursor_search);

            fillListSourceToSearch();

            mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    FirebaseDAO.getInstance().getUserByEmail(query, new DataCallback<User>() {
                        @Override
                        public void onDataReceive(User user) {
                            if(user == null){
                                Toast.makeText( MainActivity.this , getResources().getString(R.string.invalid_user), Toast.LENGTH_LONG).show();
                            }else{
                                //To send user selected from fragment to activity
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("userFound", user);
                                UserProfileFragment userProfileFragment = new UserProfileFragment();
                                userProfileFragment.setArguments(bundle);

                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.content_main, userProfileFragment).commit();
                            }
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    });

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    return false;
                }
            });

    }

    public void fillListSourceToSearch() {
        mListSource = DataLoader.getInstance().getEmails();
        String[] sourceArr = new String[mListSource.size()];
        mSearchView.setSuggestions(mListSource.toArray(sourceArr));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        addSearchBar();

        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
        mSearchView.setMenuItem(item);

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        UserLocationUtils.getInstance().revokeSendLocationListener();
        super.onDestroy();
    }
}
