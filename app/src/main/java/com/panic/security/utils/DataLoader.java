package com.panic.security.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.panic.security.entities.Crime;
import com.panic.security.entities.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by maikb on 29/09/2017.
 */

public class DataLoader {

    private String TAG = "DataLoader";

    private Activity activity;

    private static DataLoader dataLoader;

    private List<DataLoaderListener> onCompleteListeners;
    private List<DataLoaderListener> onCrimeChangedListeners;
    private Set<String> countLoadData;

    private List<String> emails;

    private List<WeightedLatLng> crimeLocationList;
    private Map<String, Double> crimeWeights;

    private int totalLoadData = 2;

    public static synchronized DataLoader getInstance() {
        return dataLoader;
    }

    public static synchronized DataLoader getInstance(Activity activity) {
        if(dataLoader == null){
            dataLoader = new DataLoader(activity);
        }
        return dataLoader;
    }

    private DataLoader (Activity activity){
        this.onCompleteListeners = new ArrayList<>();
        this.onCrimeChangedListeners = new ArrayList<>();
        this.emails = new ArrayList<>();
        this.crimeLocationList = new ArrayList<>();
        this.countLoadData = new HashSet<>();
        this.activity = activity;
    }

    public List<String> getEmails() {
        return emails;
    }

    public List<WeightedLatLng> getCrimeLocationList() {
        return crimeLocationList;
    }

    public void loadData() {

        loadEmails();
        loadMapZones();
    }

    public void loadEmails () {
        FirebaseDAO.getInstance().getAllEmailsForAllUsers(new DataCallback<List<String>>() {
            @Override
            public void onDataReceive(List<String> emails) {
                if(emails != null){
                    DataLoader.this.emails = emails;
                }
                addLoadData("loadEmails");
            }
        });
    }

    public void loadMapZones () {
        initCrimeWeights();
        FirebaseDAO.getInstance().addCrimeLocationListener (new DataCallback<Pair<Crime, Location>>() {
            @Override
            public void onDataReceive(Pair<Crime, Location> data) {
                crimeLocationList.add (getWeightedLatLng(data));
                reportListeners(onCrimeChangedListeners);
                addLoadData("loadMapZones");
            }
        });
    }

    private void initCrimeWeights () {
        crimeWeights = new HashMap<>();
        crimeWeights.put("vandalism_crime", 0.3);
        crimeWeights.put("suspicious_activity_crime", 0.3);
        crimeWeights.put("shop_lifting_crime", 0.2);
        crimeWeights.put("other_crime", 0.4);
        crimeWeights.put("assault_crime", 1.0);
        crimeWeights.put("auto_theft_crime", 0.8);
        crimeWeights.put("burglary_crime", 0.9);
        crimeWeights.put("drugs_crime", 0.7);
        crimeWeights.put("homicide_crime", 1.0);
    }

    public WeightedLatLng getWeightedLatLng (Pair<Crime, Location> data) {
        Crime crime = data.first;
        Location location = data.second;

        double weight = 0.0;
        if (crimeWeights.containsKey (crime.getType())) {
            weight = crimeWeights.get (crime.getType());
        }

        return new WeightedLatLng(
                new LatLng (location.getLatitude(), location.getLongitude()),
                weight
        );
    }

    public void addOnCompleteLoadListener(DataLoaderListener dataLoaderListener) {
        onCompleteListeners.add(dataLoaderListener);
    }

    public void addOnCrimeChangedListener (DataLoaderListener dataLoaderListener) {
        onCrimeChangedListeners.add(dataLoaderListener);
    }

    public void reportListeners(List<DataLoaderListener> listeners) {
        for(DataLoaderListener listener : listeners) {
            listener.onLoadCompleted();
        }
    }

    public synchronized void addLoadData(String id) {
        countLoadData.add(id);
        if(countLoadData.size() == totalLoadData){
            reportListeners(onCompleteListeners);
            removeListeners(onCompleteListeners);
        }
    }

    public void removeListeners(List<DataLoaderListener> listeners) {
        listeners.clear();
    }

    public boolean hasActiveInternetConnection ( ) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

}
