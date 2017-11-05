package com.panic.security.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by drdagerm on 10/25/17.
 */

public class CustomSpinnerAdapter extends ArrayAdapter {
    public CustomSpinnerAdapter(Context context, int textViewResourceId, String[] objects){
        super( context, textViewResourceId, objects );
    }

    @Override
    public int getCount(){
        return super.getCount();
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ){
        return super.getView( position, convertView, parent );
    }
}
