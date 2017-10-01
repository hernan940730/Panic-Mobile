package com.panic.security.controllers.main_module;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.panic.security.R;
import com.panic.security.controllers.friends_module.FriendsFragment;
import com.panic.security.controllers.login_sign_up_module.LoginActivity;

import com.panic.security.controllers.reports_module.ReportsFragment;
import com.panic.security.controllers.user_profile_module.UserProfileFragment;
import com.panic.security.entities.Crime;
import com.panic.security.entities.Profile;
import com.panic.security.entities.Report;
import com.panic.security.entities.User;
import com.panic.security.utils.CouchbaseDAO;
import com.panic.security.utils.DataCallback;
import com.panic.security.utils.FirebaseDAO;
import com.panic.security.utils.FirebaseReferences;
import com.panic.security.utils.UserLocationUtils;
import com.panic.security.models.map_module.MapDrawer;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, View.OnClickListener{

    private final String TAG = "MainActivity";

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
    private String crime = "";
    private String mCrimeName;

    // Authentication with FireBase
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    private CameraPosition curCameraPosition;
    private MapDrawer mapDrawer;
    private ImageButton crimes [];
    private ImageButton closeButton;
    private Animation animFadeIn ;
    private LatLng location;
    private Marker marker;
    public final int locationRequestCode = 1;

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
        crimes = new ImageButton[CRIMES_LIST.length];
        crimes[0] =( ImageButton ) findViewById( R.id.assault_button );
        crimes[1] =( ImageButton ) findViewById( R.id.auto_theft_button);
        crimes[2] =( ImageButton ) findViewById( R.id.burglary_button );
        crimes[3] =( ImageButton ) findViewById( R.id.shop_lifting_button );
        crimes[4] =( ImageButton ) findViewById( R.id.suspicious_button );
        crimes[5] =( ImageButton ) findViewById( R.id.homicide_button );
        crimes[6] =( ImageButton ) findViewById( R.id.vandalism_button );
        crimes[7] =( ImageButton ) findViewById( R.id.drugs_button );
        crimes[8] =( ImageButton ) findViewById( R.id.other_button );

        setUpCrimesButtons();

        closeButton = (ImageButton)findViewById( R.id.close_button );
        closeButton.setOnClickListener( this );

        final Button shareLocationButton = (Button) findViewById(R.id.share_location_button);
        shareLocationButton.setOnClickListener(new View.OnClickListener() {
            boolean isSharing = false;
            @Override
            public void onClick(View view) {
                if (isSharing) {
                    UserLocationUtils.getInstance().revokeSendLocationListener();
                    shareLocationButton.setText(R.string.share_location);
                    shareLocationButton.setBackgroundResource (R.color.success_color);
                }
                else {
                    UserLocationUtils.getInstance().addSendLocationListener(MainActivity.this);
                    shareLocationButton.setText(R.string.stop_share_location);
                    shareLocationButton.setBackgroundResource (R.color.failure_color);
                }
                isSharing = !isSharing;
            }
        });

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
            fragmentManager.beginTransaction().replace (R.id.content_main, new UserProfileFragment()).commit();
        } else if (id == R.id.item_friends) {
            fragmentManager.beginTransaction().replace (R.id.content_main, new FriendsFragment()).commit();
        } else if (id == R.id.item_my_reports) {
            fragmentManager.beginTransaction().replace (R.id.content_main, new ReportsFragment()).commit();
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

            if(type.equals("query")){
                User userFound = (User)intent.getSerializableExtra("user_in_search");
                Bundle bundle = new Bundle();
                bundle.putSerializable("userFound", userFound);
                UserProfileFragment userProfileFragment = new UserProfileFragment();
                userProfileFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.content_main, userProfileFragment).commit();

            }else if(type.equals("list_friends")){
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

        View locationButton = ((View) this.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        rlp.setMargins(
                0,
                getResources().getDimensionPixelSize(R.dimen.y_location_margin),
                0,
                0
        );

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
        for(int i = 0; i < CRIMES_LIST.length; i++ ){
            crimes[i].setVisibility( View.VISIBLE );
        }
        closeButton.setVisibility( View.VISIBLE );
    }

    private void setUpCrimesButtons() {

        for(int i = 0; i < CRIMES_LIST.length; i++ ){
            crimes[i].setVisibility( View.GONE );
            crimes[i].setOnClickListener( this );
        }
    }

    private void hideCrimesButtons() {
        for(int i = 0; i < CRIMES_LIST.length; i++ ){
            crimes[i].setVisibility( View.INVISIBLE );
        }
        closeButton.setVisibility( View.INVISIBLE );
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
        int i = view.getId();
        crime = "";
        switch ( i ){
            case R.id.assault_button:
                crime = CRIMES_LIST[0];
                mCrimeName = getResources().getString( R.string.assault_crime );
                break;
            case R.id.auto_theft_button:
                crime = CRIMES_LIST[1];
                mCrimeName = getResources().getString( R.string.auto_theft_crime);
                break;
            case R.id.burglary_button:
                crime = CRIMES_LIST[2];
                mCrimeName = getResources().getString( R.string.burglary_crime );
                break;
            case R.id.shop_lifting_button:
                crime = CRIMES_LIST[3];
                mCrimeName = getResources().getString( R.string.shop_lifting_crime );
                break;
            case R.id.suspicious_button:
                crime = CRIMES_LIST[4];
                mCrimeName = getResources().getString( R.string.suspicious_activity_crime );
                break;
            case R.id.homicide_button:
                crime = CRIMES_LIST[5];
                mCrimeName = getResources().getString( R.string.homicide_crime );
                break;
            case R.id.vandalism_button:
                crime = CRIMES_LIST[6];
                mCrimeName = getResources().getString( R.string.vandalism_crime );
                break;
            case R.id.drugs_button:
                crime = CRIMES_LIST[7];
                mCrimeName = getResources().getString( R.string.drugs_crime );
                break;
            case R.id.other_button:
                crime = CRIMES_LIST[8];
                mCrimeName = getResources().getString( R.string.other_crime );
                break;
            case R.id.close_button:
                marker.remove();
                isMarker = false;
                hideCrimesButtons();
                break;
        }
        // Show Calendar
        if( isMarker ){
            final Calendar c = Calendar.getInstance();
            int year = c.get( Calendar.YEAR );
            int month = c.get( Calendar.MONTH );
            int day = c.get( Calendar.DAY_OF_MONTH );
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, day);
                    showDescriptionUI(c.getTimeInMillis());
                }
            }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate( c.getTimeInMillis() );
            datePickerDialog.show();
        }
    }

    private void showDescriptionUI (final long timeInMillis){

        // Create Dialog for description input
        AlertDialog.Builder builder = new AlertDialog.Builder( this, R.style.AlertDialogStyle);
        builder.setTitle( mCrimeName + " - " + getResources().getString( R.string.reportDescriptionTitle ) );
        final EditText input = new EditText( this );
        input.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES );
        builder.setView( input );

        builder.setPositiveButton(getResources().getString( R.string.accept ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mText = input.getText().toString();
                if( !TextUtils.isEmpty(mText) ) {
                    reportCrime(crime, location, timeInMillis);
                    marker.remove();
                    Toast.makeText( MainActivity.this, R.string.report_done, Toast.LENGTH_LONG ).show();
                    hideCrimesButtons();
                }
            }
        });
        builder.setNegativeButton(getResources().getString( R.string.cancel ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void reportCrime( String crimeToReport, LatLng marker, long timeInMillis) {

        com.panic.security.entities.Location location = new com.panic.security.entities.Location();
        location.setLatitude( marker.latitude );
        location.setLongitude( marker.longitude );

        Crime crime = new Crime();
        crime.setType( crimeToReport );
        crime.setDate( timeInMillis );

        Report report = new Report();
        report.setDescription(mText);

        pushReport( report, crime, location );
    }

    public String pushReport (Report report, Crime crime, com.panic.security.entities.Location location) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference reportRef = database.getReference (FirebaseReferences.REPORTS_REFERENCE).push ();
        DatabaseReference crimeRef = database.getReference (FirebaseReferences.CRIMES_REFERENCE).push ();
        DatabaseReference locationRef = database.getReference (FirebaseReferences.LOCATIONS_REFERENCE).push ();

        location.setCrime_id(crimeRef.getKey());
        location.setId(locationRef.getKey());

        crime.setId(crimeRef.getKey());
        crime.setReport_id(reportRef.getKey());
        crime.setLocation_id(locationRef.getKey());

        report.setId(reportRef.getKey());
        report.setCrime_id(crimeRef.getKey());

        locationRef.setValue(location);
        crimeRef.setValue(crime);
        reportRef.setValue(report);
        reportRef.child(FirebaseReferences.Report.DATE_REFERENCE).setValue(ServerValue.TIMESTAMP);

        database.getReference (FirebaseReferences.USERS_REFERENCE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(FirebaseReferences.User.REPORTS_REFERENCE)
                .child(report.getId())
                .setValue(report.getId());

        return reportRef.getKey ();
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
