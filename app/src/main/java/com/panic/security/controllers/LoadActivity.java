package com.panic.security.controllers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.utils.CouchbaseDAO;
import com.panic.security.utils.DataLoader;
import com.panic.security.utils.DataLoaderListener;

public class LoadActivity extends AppCompatActivity implements DataLoaderListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        loaderCouchbase();
        
        DataLoader.getInstance().addListener(this);

        DataLoader.getInstance().loadData();

    }

    @Override
    public void onLoadCompleted() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity( intent );
        finish();
    }

    private void loaderCouchbase() {
        CouchbaseDAO.getInstance (this);
    }

}
