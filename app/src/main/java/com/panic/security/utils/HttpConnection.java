package com.panic.security.utils;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by drdagerm on 11/5/17.
 */

public class HttpConnection extends AsyncTask<String, Void, InputStream > {

    private DataCallback<InputStream> callback;

    public HttpConnection(DataCallback<InputStream> callback) {
        this.callback = callback;
    }

    @Override
    protected InputStream doInBackground(String... urls) {
        try {
            return getResponseByteArray(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(InputStream object) {
        super.onPostExecute(object);
        callback.onDataReceive(object);
    }

    public InputStream getResponseByteArray(String stringUrl) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        return getResponseByteArray(httpConn);
    }

    private InputStream getResponseByteArray(HttpURLConnection httpConn) throws IOException {
        InputStream is = null;
        if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            is = httpConn.getInputStream();
        }
        return is;
    }
}
