package com.panic.security.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by maikb on 29/09/2017.
 */

public class DataLoader {

    private List<String> emails;
    private static DataLoader dataLoader;
    private List<DataLoaderListener> listeners;
    private Set<String> countLoadData;
    private int totalLoadData = 1;

    public static synchronized DataLoader getInstance(){
        if(dataLoader == null){
            dataLoader = new DataLoader();
        }
        return dataLoader;
    }

    private DataLoader(){
        this.emails = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.countLoadData = new HashSet<>();
    }

    public List<String> getEmails() {
        return emails;
    }

    public void loadData(){
        loadEmails();
    }

    public void loadEmails(){
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

    public void addListener(DataLoaderListener dataLoaderListener){
        listeners.add(dataLoaderListener);
    }

    public void reportListeners(){
        for(DataLoaderListener listener : listeners){
            listener.onLoadCompleted();
        }
        removeListeners();
    }

    public synchronized void addLoadData(String id){
        countLoadData.add(id);
        if(countLoadData.size() == totalLoadData){
            reportListeners();
        }
    }

    public void removeListeners(){
        listeners.clear();
    }



}
