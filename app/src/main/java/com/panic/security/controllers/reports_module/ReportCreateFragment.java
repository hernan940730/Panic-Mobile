package com.panic.security.controllers.reports_module;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.panic.security.R;
import com.panic.security.entities.User;
import com.panic.security.utils.DataCallback;
import com.panic.security.utils.FirebaseDAO;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReportCreateFragment extends Fragment {

    private static final int NUM_CRIMES = 9;

    private final Calendar c = Calendar.getInstance();

    private FirebaseAuth mAuth;

    private static Integer[] iconsDatabase = {
            R.mipmap.ic_assault,
            R.mipmap.ic_auto_theft,
            R.mipmap.ic_burglary,
            R.mipmap.ic_drugs,
            R.mipmap.ic_homicide,
            R.mipmap.ic_shoplifting,
            R.mipmap.ic_suspicious_activity,
            R.mipmap.ic_vandalism,
            R.mipmap.ic_other
    };

    private static Integer[] crimesDatabase = {
            R.string.assault_crime,
            R.string.auto_theft_crime,
            R.string.burglary_crime,
            R.string.drugs_crime,
            R.string.homicide_crime,
            R.string.shop_lifting_crime,
            R.string.suspicious_activity_crime,
            R.string.vandalism_crime,
            R.string.other_crime
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int year = c.get( Calendar.YEAR );
        int month = c.get( Calendar.MONTH );
        int day = c.get( Calendar.DAY_OF_MONTH );

        final View view = inflater.inflate(R.layout.fragment_report_create, container, false);

        Spinner crimesSpinner = view.findViewById( R.id.crimes_spinner );
        ArrayAdapter<Map<String, ?>> adapter = ArrayAdapter.createFromResource();
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        crimesSpinner.setAdapter( adapter );
        //Button date = ( Button )view.findViewById( R.id.crime_date );

        //date.setText( String.valueOf( day ) + "/" + String.valueOf( month ) + "/" + String.valueOf( year ) );
        return view;
    }

}
