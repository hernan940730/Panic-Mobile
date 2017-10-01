package com.panic.security.controllers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.panic.security.R;
import com.panic.security.controllers.login_sign_up_module.LoginActivity;
import com.panic.security.utils.CouchbaseDAO;
import com.panic.security.utils.DataLoader;
import com.panic.security.utils.DataLoaderListener;


public class LoadActivity extends AppCompatActivity implements DataLoaderListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loaderCouchbase();

        DataLoader.getInstance().addListener(this);

        if(hasActiveInternetConnection()){
            DataLoader.getInstance().loadData();
        }else{
            onLoadCompleted();
        }
    }

    @Override
    public void onLoadCompleted() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity( intent );
        finish();
    }

    private void loaderCouchbase() {
        CouchbaseDAO.getInstance (this);
    }

    public boolean hasActiveInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = ( activeNetwork != null && activeNetwork.isConnectedOrConnecting() );
        return isConnected;
    }

}
